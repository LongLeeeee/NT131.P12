package com.example.project_app;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.example.project_app.data.userModel.ApiService;
import com.example.project_app.data.userModel.ForgotPasswordResponse;
import com.example.project_app.data.userModel.Monitor;
import com.example.project_app.data.userModel.SessionManager;
import com.example.project_app.data.userModel.UpdatePasswordRequest;
import com.google.android.material.textfield.TextInputEditText;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private Button btn_back, btn_change_pass;
    private TextView warning1, warning2, warning3;
    private TextInputEditText current_password, new_password, confirm_password;
    private boolean isAtleast8 = false, hasUppercase = false, hasNumber = false, hasSymbol = false, isReset = false;
    private Dialog noti_Dialog;
    private long start, total, finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_back = findViewById(R.id.back);

        warning1 = findViewById(R.id.warning1);
        warning2 = findViewById(R.id.warning2);
        warning3 = findViewById(R.id.warning3);
        btn_change_pass = findViewById(R.id.btn_change_password);
        current_password = findViewById(R.id.current_pass);
        new_password = findViewById(R.id.new_pass);
        confirm_password = findViewById(R.id.confirm_password);

        noti_Dialog= new Dialog(ChangePasswordActivity.this);
        noti_Dialog.setContentView(R.layout.dialog);
        noti_Dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        noti_Dialog.getWindow().setBackgroundDrawable(null);
        ImageView dialogicon = noti_Dialog.findViewById(R.id.icon);
        TextView dialogtitle = noti_Dialog.findViewById(R.id.title);
        TextView dialogdecript = noti_Dialog.findViewById(R.id.descript);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0){
                    fragmentManager.popBackStack();
                }
                else {
                    finish();
                    overridePendingTransition(R.anim.animation3, R.anim.animation4);
                }
            }
        });

        btn_change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateFields(current_password.getText().toString(), new_password.getText().toString(), confirm_password.getText().toString())){
                    if(validatePassword(new_password.getText().toString(), confirm_password.getText().toString())){
                        ApiService.apiService.update_password(MainActivity.token, new UpdatePasswordRequest(MainActivity.userdata.getEmail(), "change_password",new_password.getText().toString(), current_password.getText().toString())).enqueue(new Callback<ForgotPasswordResponse>() {
                            @Override
                            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                                if (response.code() == 201){
                                    dialogicon.setImageResource(R.drawable.tick_icon);
                                    dialogtitle.setText("Password Changed!");
                                    dialogdecript.setText("Your password has been changed \nsuccessfully.");
                                    noti_Dialog.show();
                                    new Handler().postDelayed(() -> {
                                        noti_Dialog.dismiss();
                                        FragmentManager fragmentManager = getSupportFragmentManager();
                                        if (fragmentManager.getBackStackEntryCount() > 0){
                                            fragmentManager.popBackStack();
                                        }
                                        else {
                                            finish();
                                        }
                                        finish();
                                    }, 3000);
                                }
                                else if (response.code() == 500 ){
                                    dialogicon.setImageResource(R.drawable.warnning_red_2);
                                    dialogtitle.setText("Waring");
                                    dialogdecript.setText("Error updating users. Please enter again to continue");
                                    noti_Dialog.show();
                                    new Handler().postDelayed(() -> {
                                        noti_Dialog.dismiss();;
                                    }, 3000);
                                }
                                else if (response.code() == 404 ){
                                    dialogicon.setImageResource(R.drawable.warnning_red_2);
                                    dialogtitle.setText("Waring");
                                    dialogdecript.setText("Your current password is incorrect. Please enter again to continue");
                                    noti_Dialog.show();
                                    new Handler().postDelayed(() -> {
                                        noti_Dialog.dismiss();
                                    }, 3000);
                                }
                                else {
                                    ForgotPasswordResponse forgotPasswordResponse = response.body();
                                    Toast.makeText(ChangePasswordActivity.this,forgotPasswordResponse.getErr() , Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<ForgotPasswordResponse> call, Throwable throwable) {
                                Toast.makeText(ChangePasswordActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    protected void onStop(){
        super.onStop();
        finish = Instant.now().getEpochSecond();
        total = finish - start;
        LocalDate today = LocalDate.now();
        int day = today.getDayOfMonth();
        int month = today.getMonthValue();
        int year = today.getYear();
        String month1 = Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        String date = day + " " + month1 + " " + year;
        if (MainActivity.type.equals("normal")){
            ApiService.apiService.updateData(new Monitor(MainActivity.userdata.getUsername(),date, 0, finish - start, 0)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                }

                @Override
                public void onFailure(Call<String> call, Throwable throwable) {

                }
            });
        }
        else if (MainActivity.type.equals("google")){
            ApiService.apiService.updateData(new Monitor(MainActivity.userSignInWithGoogle.getUsername(),date, 0, total, 0)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                }

                @Override
                public void onFailure(Call<String> call, Throwable throwable) {

                }
            });
        }
        else {
            ApiService.apiService.updateData(new Monitor(MainActivity.userSignInWithFacebook.getName(),date, 0, total, 0)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                }

                @Override
                public void onFailure(Call<String> call, Throwable throwable) {

                }
            });
        }
    }
    @Override
    protected void onStart(){
        super.onStart();
        start = Instant.now().getEpochSecond();
    }

    private boolean validateFields(String current_pass, String new_pass, String confirm_pass) {
        boolean isValid = true;

        if (current_pass.isEmpty()) {
            warning1.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            warning1.setVisibility(View.INVISIBLE);
        }

        if (new_pass.isEmpty()) {
            warning2.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            warning2.setVisibility(View.INVISIBLE);
        }

        if (confirm_pass.isEmpty()) {
            warning3.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            warning3.setVisibility(View.INVISIBLE);
        }

        return isValid;
    }

    private boolean validatePassword(String new_pass, String confirm_pass) {
        boolean isValid = true;

        if (new_pass.length() >= 8) {
            isAtleast8 = true;
        } else {
            isAtleast8 = false;
            warning2.setVisibility(View.VISIBLE);
            warning2.setText("Password must be at least 8 characters");
            isValid = false;
        }

        if (new_pass.matches(".*[A-Z].*")) {
            hasUppercase = true;
        } else {
            hasUppercase = false;
            warning2.setVisibility(View.VISIBLE);
            warning2.setText("Password must contain at least 1 uppercase letter");
            isValid = false;
        }

        if (new_pass.matches(".*\\d.*")) {
            hasNumber = true;
        } else {
            hasNumber = false;
            warning2.setVisibility(View.VISIBLE);
            warning2.setText("Password must contain at least 1 number");
            isValid = false;
        }

        if (new_pass.matches(".*[!@#$%^&*].*")) {
            hasSymbol = true;
        } else {
            hasSymbol = false;
            warning2.setVisibility(View.VISIBLE);
            warning2.setText("Password must contain at least 1 symbol");
            isValid = false;
        }

        if (!new_pass.equals(confirm_pass)) {
            warning3.setVisibility(View.VISIBLE);
            warning3.setText("Passwords do not match");
            isValid = false;
        } else {
            warning3.setVisibility(View.INVISIBLE);
        }
        return isValid;
    }
}