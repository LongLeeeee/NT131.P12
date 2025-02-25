package com.example.project_app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project_app.data.userModel.ApiService;
import com.example.project_app.data.userModel.ForgotPasswordResponse;
import com.example.project_app.data.userModel.NormalLoginResponse;
import com.example.project_app.data.userModel.SessionManager;
import com.example.project_app.data.userModel.SignUpRequest;
import com.example.project_app.data.userModel.UpdatePasswordRequest;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {
    TextInputEditText password, confirm_password;
    TextView password_error, confirm_password_error;
    Button reset_password, back;
    String email;
    Dialog successdialog;
    private boolean isAtleast8 = false, hasUppercase = false, hasNumber = false, hasSymbol = false, isReset = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = getIntent().getStringExtra("email");

        password = findViewById(R.id.new_password);
        confirm_password = findViewById(R.id.confirm_password);
        password_error = findViewById(R.id.new_password_error);
        confirm_password_error = findViewById(R.id.confirm_password_error);
        reset_password = findViewById(R.id.reset_password);
        back = findViewById(R.id.back);
        successdialog = new Dialog(ResetPasswordActivity.this);
        successdialog.setContentView(R.layout.dialog);
        successdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        successdialog.getWindow().setBackgroundDrawable(null);
        ImageView dialogicon = successdialog.findViewById(R.id.icon);
        TextView dialogtitle = successdialog.findViewById(R.id.title);
        TextView dialogdecript = successdialog.findViewById(R.id.descript);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Back.Back_Pressed(ResetPasswordActivity.this, ForgotActivity.class);
                overridePendingTransition(R.anim.animation3, R.anim.animation4);
                finish();
            }
        });

        reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass = password.getText().toString();
                String confirm_pass = confirm_password.getText().toString();

                if (validateFields(pass, confirm_pass)) {
                    if (validatePassword(pass, confirm_pass)) {
                        isReset = true;
                        //Call API
                        ApiService.apiService.update_password("", new UpdatePasswordRequest(email,"forgot_password", pass, "")).enqueue(new Callback<ForgotPasswordResponse>() {
                            @Override
                            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                                if (response.code() == 201){
                                    dialogicon.setImageResource(R.drawable.tick_icon);
                                    dialogtitle.setText("Password Reset!");
                                    dialogdecript.setText("Your password has been reset successfully");
                                    successdialog.setOnShowListener(dialog -> {
                                        View view = successdialog.getWindow().getDecorView();
                                        view.setOnTouchListener((v, event) -> {
                                            successdialog.dismiss();
                                            Intent intent = new Intent(ResetPasswordActivity.this, WelcomeActivity.class);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.animation3, R.anim.animation4);
                                            finish();
                                            return true;
                                        });
                                    });
                                    successdialog.show();
                                }
                                else if (response.code() == 401){
                                    new SessionManager(ResetPasswordActivity.this).Logout();
                                }
                                else {
                                    ForgotPasswordResponse forgotPasswordResponse = response.body();
                                    Toast.makeText(ResetPasswordActivity.this,forgotPasswordResponse.getErr() , Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<ForgotPasswordResponse> call, Throwable throwable) {

                            }
                        });
                    }
                }
            }
        });
    }


    private boolean validateFields(String pass, String confirm_pass) {
        boolean isValid = true;

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