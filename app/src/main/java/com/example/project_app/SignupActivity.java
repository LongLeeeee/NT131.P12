package com.example.project_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project_app.data.userModel.ApiService;
import com.example.project_app.data.userModel.LoginWithFacebookRequest;
import com.example.project_app.data.userModel.LoginWithFacebookResponse;
import com.example.project_app.data.userModel.LoginWithGoogleRequest;
import com.example.project_app.data.userModel.LoginWithGoogleResponse;
import com.example.project_app.data.userModel.NormalLoginResponse;
import com.example.project_app.data.userModel.SignUpRequest;
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
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    TextInputEditText username, email, password, confirm_password;
    TextView username_error, email_error, password_error, confirm_password_error;
    CheckBox checkbox;
    Button signup, back;
    private boolean isAtleast8 = false, hasUppercase = false, hasNumber = false, hasSymbol = false, isSignup = false;

    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        checkbox = findViewById(R.id.checkbox);
        checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                Toast.makeText(SignupActivity.this, "Checkbox is unchecked", Toast.LENGTH_SHORT).show();
                isSignup = false;
            } else {
                Toast.makeText(SignupActivity.this, "Checkbox is checked", Toast.LENGTH_SHORT).show();
                isSignup = true;
            }
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(view -> {
            Back.Back_Pressed(SignupActivity.this, WelcomeActivity.class);
            overridePendingTransition(R.anim.animation3, R.anim.animation4);
            finish();
        });

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        username_error = findViewById(R.id.username_error);
        email_error = findViewById(R.id.email_error);
        password_error = findViewById(R.id.password_error);
        confirm_password_error = findViewById(R.id.confirm_password_error);
        signup = findViewById(R.id.signup);


        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);



        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String mail = email.getText().toString();
                String pass = password.getText().toString();
                String confirm_pass = confirm_password.getText().toString();

                if (validateFields(user, mail, pass, confirm_pass)) {
                    if (validatePassword(pass, confirm_pass)) {
                        SignUpRequest signUpRequest = new SignUpRequest(user, mail, pass);
                        ApiService.apiService.sign_up(signUpRequest).enqueue(new Callback<NormalLoginResponse>() {
                            @Override
                            public void onResponse(Call<NormalLoginResponse> call, Response<NormalLoginResponse> response) {
                                if (response.code() == 201) {
                                    NormalLoginResponse normalLoginResponse = response.body();
                                    Intent intent = new Intent(SignupActivity.this, WelcomeActivity.class);
                                    startActivity(intent);
                                } else if (response.code() == 500) {
                                    Toast.makeText(SignupActivity.this, "Server error ", Toast.LENGTH_SHORT).show();
                                } else if (response.code() == 400) {
                                    Toast.makeText(SignupActivity.this, "'Missing required component", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<NormalLoginResponse> call, Throwable throwable) {
                                Toast.makeText(SignupActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });




    }

    private void signInWithGoogle(){
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent,200);
    }

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
                        if (response.code() == 201){
                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else {
                            LoginWithGoogleResponse res = response.body();
                            Toast.makeText(SignupActivity.this, "Sign in failed" , Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginWithGoogleResponse> call, Throwable throwable) {
                        Toast.makeText(SignupActivity.this, "Call api error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch (ApiException e){
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateFields(String user, String mail, String pass, String confirm_pass) {
        boolean isValid = true;

        if (user.isEmpty()) {
            username_error.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            username_error.setVisibility(View.INVISIBLE);
        }

        if (mail.isEmpty()) {
            email_error.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            email_error.setVisibility(View.INVISIBLE);
        }

        if (pass.isEmpty()) {
            password_error.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            password_error.setVisibility(View.INVISIBLE);
        }

        if (confirm_pass.isEmpty()) {
            confirm_password_error.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            confirm_password_error.setVisibility(View.INVISIBLE);
        }

        if (!isSignup){
            Toast.makeText(getApplicationContext(), "Please agree to our terms and policies.", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private boolean validatePassword(String pass, String confirm_pass) {
        boolean isValid = true;

        if (pass.length() >= 8) {
            isAtleast8 = true;
        } else {
            isAtleast8 = false;
            password_error.setVisibility(View.VISIBLE);
            password_error.setText("Password must be at least 8 characters");
            isValid = false;
        }

        if (pass.matches(".*[A-Z].*")) {
            hasUppercase = true;
        } else {
            hasUppercase = false;
            password_error.setVisibility(View.VISIBLE);
            password_error.setText("Password must contain at least 1 uppercase letter");
            isValid = false;
        }

        if (pass.matches(".*\\d.*")) {
            hasNumber = true;
        } else {
            hasNumber = false;
            password_error.setVisibility(View.VISIBLE);
            password_error.setText("Password must contain at least 1 number");
            isValid = false;
        }

        if (pass.matches(".*[!@#$%^&*].*")) {
            hasSymbol = true;
        } else {
            hasSymbol = false;
            password_error.setVisibility(View.VISIBLE);
            password_error.setText("Password must contain at least 1 symbol");
            isValid = false;
        }

        if (!pass.equals(confirm_pass)) {
            confirm_password_error.setVisibility(View.VISIBLE);
            confirm_password_error.setText("Passwords do not match");
            isValid = false;
        } else {
            confirm_password_error.setVisibility(View.INVISIBLE);
        }

        return isValid;
    }
}