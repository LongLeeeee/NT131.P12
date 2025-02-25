package com.example.project_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.example.project_app.data.userModel.ApiService;
import com.example.project_app.data.userModel.Monitor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetectionSensitivityActivity extends AppCompatActivity {

    private Button btn_back;
    private SwitchCompat switchDetection;
    private RadioGroup radioGroup;
    private RadioButton radioLow, radioMedium, radioHigh;
    private long start, total, finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detection_sensitivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_back = findViewById(R.id.back);
        switchDetection = findViewById(R.id.switch_detection);
        radioGroup = findViewById(R.id.radio_group);
        radioLow = findViewById(R.id.radio_low);
        radioMedium = findViewById(R.id.radio_medium);
        radioHigh = findViewById(R.id.radio_high);

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

        setRadioButtonsEnabled(false);

        switchDetection.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setRadioButtonsEnabled(isChecked);
            if (!isChecked) {
                radioGroup.clearCheck(); // Xóa lựa chọn khi Switch tắt
            }        });

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            changeRadioButtonColors(checkedId);
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

    private void setRadioButtonsEnabled(boolean isEnabled) {
        radioLow.setEnabled(isEnabled);
        radioMedium.setEnabled(isEnabled);
        radioHigh.setEnabled(isEnabled);
    }

    private void changeRadioButtonColors(int checkedId) {
        if (checkedId == R.id.radio_low) {
            radioLow.setButtonTintList(getResources().getColorStateList(R.color.green, null));
            radioMedium.setButtonTintList(getResources().getColorStateList(R.color.gray, null));
            radioHigh.setButtonTintList(getResources().getColorStateList(R.color.gray, null));
        } else if (checkedId == R.id.radio_medium) {
            radioLow.setButtonTintList(getResources().getColorStateList(R.color.gray, null));
            radioMedium.setButtonTintList(getResources().getColorStateList(R.color.green, null));
            radioHigh.setButtonTintList(getResources().getColorStateList(R.color.gray, null));
        } else if (checkedId == R.id.radio_high) {
            radioLow.setButtonTintList(getResources().getColorStateList(R.color.gray, null));
            radioMedium.setButtonTintList(getResources().getColorStateList(R.color.gray, null));
            radioHigh.setButtonTintList(getResources().getColorStateList(R.color.green, null));
        }
    }
}