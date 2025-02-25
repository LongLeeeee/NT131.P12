package com.example.project_app;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project_app.data.userModel.ApiService;
import com.example.project_app.data.userModel.LoginWithFacebookRequest;
import com.example.project_app.data.userModel.LoginWithFacebookResponse;
import com.example.project_app.data.userModel.LoginWithGoogleRequest;
import com.example.project_app.data.userModel.LoginWithGoogleResponse;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.codehaus.janino.Mod;

import java.util.Arrays;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WelcomeActivity extends AppCompatActivity {

    private Button signin;
    private Button signup;
    private Button signInWithGoogle;
    private Button signInWithFacebook;
    CallbackManager callbackManager;
    private ProgressBar progressBar;
    private LinearLayout language;
    private Dialog dialog_language;
    public static boolean checked = false;

    private ImageView lang_img;
    private TextView lang_code;
    private RadioButton vie_lang, eng_lang;

    int style;

    static String facebook_name;
    static String google_name;


    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        signin = findViewById(R.id.signin_button);
        signup = findViewById(R.id.signup_button);
        signInWithGoogle = findViewById(R.id.gg_button);
        signInWithFacebook = findViewById(R.id.fb_button);
        progressBar = findViewById(R.id.progress_bar);
        lang_img = findViewById(R.id.img_language);
        lang_code = findViewById(R.id.lang_code);

        dialog_language = new Dialog(WelcomeActivity.this);
        dialog_language.setContentView(R.layout.lang_dialog);
        dialog_language.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog_language.getWindow().setBackgroundDrawable(null);
        RelativeLayout vie = dialog_language.findViewById(R.id.vie_container);
        RelativeLayout eng = dialog_language.findViewById(R.id.eng_container);
        vie_lang = dialog_language.findViewById(R.id.btn_vie);
        eng_lang = dialog_language.findViewById(R.id.btn_eng);

        vie_lang.setEnabled(false);
        eng_lang.setEnabled(false);

        language = findViewById(R.id.language);

        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                String lang_code_pre = preferences.getString("lang_code", "en");
                if (lang_code_pre.equals("vi")){
                    vie_lang.setChecked(true);
                }
                else{
                    eng_lang.setChecked(true);
                }
                dialog_language.show();
            }
        });

        vie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                if (vie_lang.isChecked() == true){
                    vie_lang.setChecked(false);
                }
                else {
                    vie_lang.setChecked(true);
                    setLocale("vi");
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("lang_code","vi");
                    editor.apply();
                    if (eng_lang.isChecked()){
                        eng_lang.setChecked(false);
                    }
                }
                dialog_language.dismiss();
            }
        });

        eng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                if (eng_lang.isChecked() == true){
                    eng_lang.setChecked(false);
                }
                else{
                    eng_lang.setChecked(true);
                    setLocale("en");
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("lang_code","en");
                    editor.apply();
                    if (vie_lang.isChecked()){
                        vie_lang.setChecked(false);
                    }
                }
                dialog_language.dismiss();
            }
        });

        //login google
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, SigninActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.animation1, R.anim.animation2);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, SignupActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.animation1, R.anim.animation2);
            }
        });

        signInWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                signInWithGoogle();
            }
        });

        //login facebook
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        ApiService.apiService.facebook_sign_in(new LoginWithFacebookRequest(accessToken.getToken()))
                                .enqueue(new Callback<LoginWithFacebookResponse>() {
                                    @Override
                                    public void onResponse(Call<LoginWithFacebookResponse> call, Response<LoginWithFacebookResponse> response) {
                                        LoginWithFacebookResponse res = response.body();
                                        if (res != null){

                                            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));

                                            facebook_name = res.getUserInfo().getName();
                                            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                                            intent.putExtra("type", res.getType());
                                            intent.putExtra("token", response.headers().get("Authorization"));
                                            intent.putExtra("user_data", response.body().getUserInfo());

                                            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.clear();
                                            editor.apply();
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.animation1, R.anim.animation2);

                                        }
                                        else {
                                            Toast.makeText(WelcomeActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<LoginWithFacebookResponse> call, Throwable throwable) {
                                        Toast.makeText(WelcomeActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    @Override
                    public void onCancel() {
                        Toast.makeText(WelcomeActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(@NonNull FacebookException e) {
                        Toast.makeText(WelcomeActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
        signInWithFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(WelcomeActivity.this, Arrays.asList("email"));
            }
        });
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        if (!checked){
            String lang_code_pre = preferences.getString("lang_code", "en");
            if (lang_code_pre != null){
                if (lang_code_pre.equals("vi")){
                    setLocale("vi");
                }
                else if (lang_code_pre.equals("en")){
                    setLocale("en");
                }
            }
        }
        else {
            String lang_code_pre = preferences.getString("lang_code", "en");
            if (lang_code_pre.equals("vi")){
                lang_img.setImageResource(R.drawable.vi_lang);
                lang_code.setText("VIE");
            }
            else {
                lang_img.setImageResource(R.drawable.en_lang);
                lang_code.setText("ENG");
            }
        }

    }


    private void signInWithGoogle(){
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent,200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                ApiService.apiService.google_sign_in(new LoginWithGoogleRequest(account.getIdToken())).enqueue(new Callback<LoginWithGoogleResponse>() {
                    @Override
                    public void onResponse(Call<LoginWithGoogleResponse> call, Response<LoginWithGoogleResponse> response) {
                        LoginWithGoogleResponse res = response.body();
                        Log.e("Data", res.getUserInfo().getDateOfBirth() + " " + res.getUserInfo().getPhoneNumber());
                        if (res != null){
                            google_name = res.getUserInfo().getUsername();
                            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                            intent.putExtra("type", res.getType());
                            intent.putExtra("token", response.headers().get("Authorization"));
                            intent.putExtra("user_data", response.body().getUserInfo());

                            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.apply();
                            startActivity(intent);
                            overridePendingTransition(R.anim.animation1, R.anim.animation2);
                            progressBar.setVisibility(View.GONE);
                        }
                        else {
                            Toast.makeText(WelcomeActivity.this, "Sign in failed" , Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginWithGoogleResponse> call, Throwable throwable) {
                        Toast.makeText(WelcomeActivity.this, "Call api error", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
            catch (ApiException e){
                progressBar.setVisibility(View.GONE);
            }
        }
    }
    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(config, dm);

        Intent intent = new Intent(this, WelcomeActivity.class);
        checked = true;
        startActivity(intent);
        finish();
    }
}