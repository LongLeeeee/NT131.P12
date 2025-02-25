package com.example.project_app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project_app.data.userModel.ApiService;
import com.example.project_app.data.userModel.ForgotPasswordRequest;
import com.example.project_app.data.userModel.ForgotPasswordResponse;
import com.example.project_app.data.userModel.NormalLoginResponse;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotActivity extends AppCompatActivity {
    TextInputEditText email;
    TextView email_error;
    Button continue_btn, back;
    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = findViewById(R.id.email);
        continue_btn = findViewById(R.id.continue_btn);
        email_error = findViewById(R.id.email_error);
        back = findViewById(R.id.back);
        dialog= new Dialog(ForgotActivity.this);
        dialog.setContentView(R.layout.dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(null);
        ImageView dialogicon = dialog.findViewById(R.id.icon);
        TextView dialogtitle = dialog.findViewById(R.id.title);
        TextView dialogdecript = dialog.findViewById(R.id.descript);

        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = email.getText().toString();
                // Check if the email is valid (LongLe)
                if (mail.contains("@") && mail.contains(".") && mail.contains("com")) {
                    // Send email to the user
                    ApiService.apiService.forgot_password(new ForgotPasswordRequest(mail))
                            .enqueue(new Callback<ForgotPasswordResponse>() {
                                @Override
                                public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                                    if (response.code() == 200){
                                        Intent intent = new Intent(ForgotActivity.this, VerificationCodeActivity.class);
                                        intent.putExtra("email", mail);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.animation1, R.anim.animation2);
                                        finish();
                                    }
                                    else {
                                        ForgotPasswordResponse forgotPasswordResponse = response.body();
                                        Toast.makeText(ForgotActivity.this,forgotPasswordResponse.getErr() , Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onFailure(Call<ForgotPasswordResponse> call, Throwable throwable) {
                                    dialogicon.setImageResource(R.drawable.warnning_red_2);
                                    dialogtitle.setText("Error");
                                    dialogdecript.setText("Call API error");
                                    dialog.show();
                                }
                            });
                } else {
                    if (mail.isEmpty()) {
                        email_error.setVisibility(View.VISIBLE);
                    }
                    else {
                        email_error.setVisibility(View.INVISIBLE);
                    }

                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Back.Back_Pressed(ForgotActivity.this, SigninActivity.class);
                overridePendingTransition(R.anim.animation3, R.anim.animation4);
                finish();
            }
        });
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