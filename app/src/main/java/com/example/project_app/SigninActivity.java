package com.example.project_app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project_app.data.userModel.UserInforCurrentForMap;
import com.google.android.material.textfield.TextInputEditText;

import com.example.project_app.data.userModel.ApiService;
import com.example.project_app.data.userModel.NormalLoginRequest;
import com.example.project_app.data.userModel.NormalLoginResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SigninActivity extends AppCompatActivity {
     TextInputEditText username, password;
     TextView username_error, password_error, forgotpass_text;
     Button signin, back;
     Dialog errorDialog;
     private boolean isSignIn = false;
     public static String usernameCurrent;


     SwitchCompat switch_remember_me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        String token = preferences.getString("token", "");

        errorDialog= new Dialog(SigninActivity.this);
        errorDialog.setContentView(R.layout.dialog);
        errorDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        errorDialog.getWindow().setBackgroundDrawable(null);
        ImageView dialogicon = errorDialog.findViewById(R.id.icon);
        TextView dialogtitle = errorDialog.findViewById(R.id.title);
        TextView dialogdecript = errorDialog.findViewById(R.id.descript);

        if (isLoggedIn){
            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            String username = sharedPreferences.getString("username", null);
            String password = sharedPreferences.getString("password", null);
            NormalLoginRequest normalLoginRequest = new NormalLoginRequest(username, password);
            ApiService.apiService.login(normalLoginRequest).enqueue(new Callback<NormalLoginResponse>() {
                @Override
                public void onResponse(Call<NormalLoginResponse> call, Response<NormalLoginResponse> response) {
                    if (response.code() == 200){
                        usernameCurrent = response.body().getUser().getUsername();

                        String token = response.headers().get("Authorization");
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("token", token);
                        editor.putString("type", response.body().getType());
                        Gson gson = new Gson();
                        String userdata = gson.toJson(response.body().getUser());
                        editor.putString("user_data", userdata);
                        editor.apply();

                        Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
                @Override
                public void onFailure(Call<NormalLoginResponse> call, Throwable throwable) {
                    dialogicon.setImageResource(R.drawable.warnning_red_2);
                    dialogtitle.setText("Error");
                    dialogdecript.setText("Login failed");
                    errorDialog.show();
                }
            });
        }
        else {
            setContentView(R.layout.activity_signin);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
            username = findViewById(R.id.username);
            password = findViewById(R.id.password);
            signin = findViewById(R.id.signin);
            username_error = findViewById(R.id.username_error);
            password_error = findViewById(R.id.password_error);
            forgotpass_text = findViewById(R.id.forgotpass_text);
            back = findViewById(R.id.back);
            switch_remember_me = findViewById(R.id.switch_remember_me);


            signin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String user = username.getText().toString();
                    String pass = password.getText().toString();

                    if (!user.isEmpty() && !pass.isEmpty()) {
                        NormalLoginRequest normalLoginRequest = new NormalLoginRequest(user, pass);
                        ApiService.apiService.login(normalLoginRequest).enqueue(new Callback<NormalLoginResponse>() {
                            @Override
                            public void onResponse(Call<NormalLoginResponse> call, Response<NormalLoginResponse> response) {
                                if (response.code() == 200) {
                                    usernameCurrent = user;
                                    String token = response.headers().get("Authorization");
                                    if (switch_remember_me.isChecked()) {
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putBoolean("isLoggedIn", true);
                                        editor.putString("token", token);
                                        editor.putString("type", response.body().getType());
                                        editor.putString("username", user);
                                        editor.putString("password", pass);
                                        Gson gson = new Gson();
                                        String userdata = gson.toJson(response.body().getUser());
                                        editor.putString("user_data", userdata);
                                        editor.apply();
                                    }

                                    Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra("token", token);
                                    intent.putExtra("type", response.body().getType());
                                    intent.putExtra("user_data", response.body().getUser());
                                    startActivity(intent);
                                    finish();
                                } else if (response.code() == 404) {
                                    dialogicon.setImageResource(R.drawable.warnning_red_2);
                                    dialogtitle.setText("Error");
                                    dialogdecript.setText("Incorrect username or password");
                                    errorDialog.show();
                                } else if (response.code() == 500) {
                                    //Toast.makeText(SigninActivity.this, "Server error ", Toast.LENGTH_SHORT).show();
                                    dialogicon.setImageResource(R.drawable.warnning_red_2);
                                    dialogtitle.setText("Error");
                                    dialogdecript.setText("Server error");
                                    errorDialog.show();
                                }
                            }

                            @Override
                            public void onFailure(Call<NormalLoginResponse> call, Throwable throwable) {
                                dialogicon.setImageResource(R.drawable.warnning_red_2);
                                dialogtitle.setText("Error");
                                dialogdecript.setText("Login failed");
                                errorDialog.show();
                            }
                        });
                    } else {
                        if (user.isEmpty()) {
                            username_error.setVisibility(View.VISIBLE);
                        } else {
                            username_error.setVisibility(View.INVISIBLE);
                        }
                        if (pass.isEmpty()) {
                            password_error.setVisibility(View.VISIBLE);
                        } else {
                            password_error.setVisibility(View.INVISIBLE);
                        }

                    }
                }
            });
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Back.Back_Pressed(SigninActivity.this, WelcomeActivity.class);
                    overridePendingTransition(R.anim.animation3, R.anim.animation4);
                    finish();
                }
            });

            forgotpass_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SigninActivity.this, ForgotActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.animation1, R.anim.animation2);
                    finish();
                }
            });
        }
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view instanceof EditText) {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    view.clearFocus();
                    hideKeyboard2(view);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void hideKeyboard2(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
