package com.example.project_app;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.project_app.data.userModel.ApiService;
import com.example.project_app.data.userModel.Monitor;
import com.example.project_app.data.userModel.NormalUser;
import com.example.project_app.data.userModel.UserSignInWithFacebook;
import com.example.project_app.data.userModel.UserSignInWithGoogle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.protobuf.Api;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText username, dateOfBirth, email, phone_number;
    private TextView title_name, warning1, warning2, warning3, warning4;
    private ImageView edit_username, edit_dateOfBirth, edit_email, edit_phone_number;
    boolean isEdited_username = false, isEdited_dateOfBirth = false, isEdited_email = false, isEdited_phone_number = false;
    private Button btn_back;
    private NormalUser userdata;
    private UserSignInWithFacebook userSignInWithFacebook;
    private UserSignInWithGoogle userSignInWithGoogle;
    private long start;
    private long finish;
    private long total;
    private Button btn_update_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        title_name = findViewById(R.id.title_name);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        dateOfBirth = findViewById(R.id.date_of_birth);
        phone_number = findViewById(R.id.phone_number);
        btn_update_profile = findViewById(R.id.btn_update_profile);

        btn_update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserSignInWithGoogle user = new UserSignInWithGoogle(email.getText().toString(), username.getText().toString(), dateOfBirth.getText().toString(), phone_number.getText().toString());
                ApiService.apiService.updateProfile(MainActivity.token, "update_profile",MainActivity.type,user).enqueue(new Callback<UserSignInWithGoogle>() {
                    @Override
                    public void onResponse(Call<UserSignInWithGoogle> call, Response<UserSignInWithGoogle> response) {
                        if (response.code() == 201){
                            title_name.setText(response.body().getUsername());
                            username.setText(response.body().getUsername());
                            email.setText(response.body().getEmail());
                            dateOfBirth.setText(response.body().getDateOfBirth());
                            phone_number.setText(response.body().getPhoneNumber());

                            if (MainActivity.type.equals("google")){
                                MainActivity.userSignInWithGoogle.setDateOfBirth(response.body().getDateOfBirth());
                                MainActivity.userSignInWithGoogle.setPhoneNumber(response.body().getPhoneNumber());
                                MainActivity.userSignInWithGoogle.setUsername(response.body().getUsername());
                                MainActivity.userSignInWithGoogle.setEmail(response.body().getEmail());
                            }
                            else if (MainActivity.type.equals("normal")){
                                MainActivity.userdata.setDateOfBirth(response.body().getDateOfBirth());
                                MainActivity.userdata.setPhoneNumber(response.body().getPhoneNumber());
                                MainActivity.userdata.setUsername(response.body().getUsername());
                                MainActivity.userdata.setEmail(response.body().getEmail());
                            }

                            dateOfBirth.setEnabled(false);
                            isEdited_dateOfBirth = false;
                            edit_dateOfBirth.setImageResource(R.drawable.edit_fill);

                            phone_number.setEnabled(false);
                            isEdited_phone_number = false;
                            edit_phone_number.setImageResource(R.drawable.edit_fill);

                            email.setEnabled(false);
                            isEdited_email = false;
                            edit_email.setImageResource(R.drawable.edit_fill);

                            username.setEnabled(false);
                            isEdited_username = false;
                            edit_username.setImageResource(R.drawable.edit_fill);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserSignInWithGoogle> call, Throwable throwable) {

                    }
                });
            }
        });

        if (MainActivity.type.equals("normal")){
            userdata = MainActivity.userdata;
            username.setText(userdata.getUsername());
            title_name.setText(userdata.getUsername());
            email.setText(userdata.getEmail());
            if(userdata.getDateOfBirth() != null){
                dateOfBirth.setText(userdata.getDateOfBirth());
            }
            if (userdata.getPhoneNumber() !=null){
                phone_number.setText(userdata.getPhoneNumber());
            }
        }
        else if (MainActivity.type.equals("facebook")){
            userSignInWithFacebook = MainActivity.userSignInWithFacebook;
            username.setText(userSignInWithFacebook.getName());
            title_name.setText(userSignInWithFacebook.getName());
            email.setText(userSignInWithFacebook.getEmail());
            if (userSignInWithFacebook.getDateOfBirth() != null){
                dateOfBirth.setText(userSignInWithFacebook.getDateOfBirth());
            }
            if (userSignInWithFacebook.getPhoneNumber() != null){
                phone_number.setText(userSignInWithFacebook.getPhoneNumber());
            }
        }
        else {
            userSignInWithGoogle = MainActivity.userSignInWithGoogle;
            username.setText(userSignInWithGoogle.getUsername());
            title_name.setText(userSignInWithGoogle.getUsername());
            email.setText(userSignInWithGoogle.getEmail());
            if (userSignInWithGoogle.getDateOfBirth()!=null){
                dateOfBirth.setText(userSignInWithGoogle.getDateOfBirth());
            }
            if (userSignInWithGoogle.getPhoneNumber() != null){
                phone_number.setText(userSignInWithGoogle.getPhoneNumber());
            }
        }

        edit_email = findViewById(R.id.edit_email);
        edit_username = findViewById(R.id.edit_username);
        edit_dateOfBirth = findViewById(R.id.edit_date_of_birth);
        edit_phone_number = findViewById(R.id.edit_phone_number);

        btn_back = findViewById(R.id.back);

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

        edit_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEdited_username){
                    username.setEnabled(true);
                    edit_username.setImageResource(R.drawable.baseline_cancel_24);
                    username.requestFocus();

                    InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(username, InputMethodManager.SHOW_IMPLICIT);
                    isEdited_username = true;
                }
                else {
                    username.setEnabled(false);
                    isEdited_username = false;
                    edit_username.setImageResource(R.drawable.edit_fill);
                }
            }
        });

        edit_dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEdited_dateOfBirth){
                    dateOfBirth.setEnabled(true);
                    edit_dateOfBirth.setImageResource(R.drawable.baseline_cancel_24);
                    dateOfBirth.requestFocus();

                    InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(dateOfBirth, InputMethodManager.SHOW_IMPLICIT);
                    isEdited_dateOfBirth = true;
                }
                else {
                    dateOfBirth.setEnabled(false);
                    isEdited_dateOfBirth = false;
                    edit_dateOfBirth.setImageResource(R.drawable.edit_fill);
                }
            }
        });

        edit_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEdited_email){
                    email.setEnabled(true);
                    edit_email.setImageResource(R.drawable.baseline_cancel_24);
                    email.requestFocus();

                    InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(email, InputMethodManager.SHOW_IMPLICIT);
                    isEdited_email = true;
                }
                else {
                    email.setEnabled(false);
                    isEdited_email = false;
                    edit_email.setImageResource(R.drawable.edit_fill);
                }
            }
        });

        edit_phone_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEdited_phone_number){
                    phone_number.setEnabled(true);
                    edit_phone_number.setImageResource(R.drawable.baseline_cancel_24);
                    phone_number.requestFocus();

                    InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(phone_number, InputMethodManager.SHOW_IMPLICIT);
                    isEdited_phone_number = true;
                }
                else {
                    phone_number.setEnabled(false);
                    isEdited_phone_number = false;
                    edit_phone_number.setImageResource(R.drawable.edit_fill);
                }
            }
        });
    }
    @Override
    protected void onStart(){
        super.onStart();
        start = Instant.now().getEpochSecond();
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
            ApiService.apiService.updateData(new Monitor(userdata.getUsername(),date, 0, total, 0)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                }

                @Override
                public void onFailure(Call<String> call, Throwable throwable) {

                }
            });
        }
        else if (MainActivity.type.equals("google")){
            ApiService.apiService.updateData(new Monitor(userSignInWithGoogle.getUsername(),date, 0, total, 0)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                }

                @Override
                public void onFailure(Call<String> call, Throwable throwable) {

                }
            });
        }
        else {
            ApiService.apiService.updateData(new Monitor(userSignInWithFacebook.getName(),date, 0, total, 0)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                }

                @Override
                public void onFailure(Call<String> call, Throwable throwable) {

                }
            });
        }
    }
}