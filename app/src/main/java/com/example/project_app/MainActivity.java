package com.example.project_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;


import com.example.project_app.Mapdata.LocationSearch;
import com.example.project_app.Mapdata.SharedViewModel;

import com.example.project_app.data.userModel.ApiService;
import com.example.project_app.data.userModel.Monitor;
import com.example.project_app.data.userModel.NormalUser;

import com.example.project_app.data.userModel.UserSignInWithFacebook;
import com.example.project_app.data.userModel.UserSignInWithGoogle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.protobuf.Api;

import org.mapsforge.core.model.LatLong;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    static String token;
    static String type;
    static NormalUser userdata;
    static UserSignInWithFacebook userSignInWithFacebook;
    static UserSignInWithGoogle userSignInWithGoogle;

    private Fragment activeFragment;
    static LocationSearch locationSearch;
    private static final String ACTIVE_FRAGMENT_TAG = "active_fragment_tag";
    private String activeFragmentTag = "DASHBOARD";

    private long start;
    private long finish;
    private long total;

    static Boolean isSharing;
    static Boolean isAlert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if (savedInstanceState != null) {
            activeFragmentTag = savedInstanceState.getString(ACTIVE_FRAGMENT_TAG);
        }

        //kiem tra file map da ducoc download chua
        File mapFile = new File(getApplicationContext().getFilesDir(), "langdaihoc.map");
        if(mapFile.exists()){
            Toast.makeText(getApplicationContext(), "Map already exists", Toast.LENGTH_SHORT).show();

        }else{
            DownloadMap();
        }

        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn){
            type = preferences.getString("type", null);
            token = preferences.getString("token", null);
            String json = preferences.getString("user_data", null);
            if (json != null) {
                Gson gson = new Gson();
                userdata = gson.fromJson(json, NormalUser.class);

            }
        }
        else {
            token = getIntent().getStringExtra("token");
            type = getIntent().getStringExtra("type");
            if (type.equals("normal")){
                userdata = (NormalUser) getIntent().getSerializableExtra("user_data");

            }
            else if (type.equals("facebook")){
                userSignInWithFacebook = (UserSignInWithFacebook) getIntent().getSerializableExtra("user_data");
            }
            else {
                userSignInWithGoogle = (UserSignInWithGoogle) getIntent().getSerializableExtra("user_data");
            }
        }

        if (savedInstanceState == null && type.equals("normal")) {

            Log.d("MainActivity", "Passing userdata: " + userdata);

            DashboardFragment dashboardFragment = new DashboardFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("user_data", userdata);
            dashboardFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, dashboardFragment)
                    .commit();
            
        }

        if (getIntent().getBooleanExtra("show_history_fragment", false)) {
            activeFragmentTag = "HISTORY";
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HistoryFragment(), "HISTORY")
                    .commit();
        }
//        if (getIntent().getBooleanExtra("show_settings_fragment", false)) {
//            activeFragmentTag = "SETTINGS";
//
//            SettingsFragment settingsFragment = new SettingsFragment();
//            Bundle settingsBundle = new Bundle();
//            settingsBundle.putSerializable("user_data", userdata);
//            settingsFragment.setArguments(settingsBundle);
//
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, settingsFragment, "SETTINGS")
//                    .commit();
//        }


  
  
        Fragment selectedFragment;
        switch (activeFragmentTag) {
            case "MAP":
                selectedFragment = getSupportFragmentManager().findFragmentByTag("MAP");
                if (selectedFragment == null) {
                    selectedFragment = new MapFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment, "MAP")
                            .commit();
                }
                break;
            case "HISTORY":
                selectedFragment = new HistoryFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment, "HISTORY")
                        .commit();
                break;
            case "SETTINGS":
                selectedFragment = new SettingsFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment, "SETTINGS")
                        .commit();
                break;
            default:
                selectedFragment = new DashboardFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment, "DASHBOARD")
                        .commit();

                BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
                bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;

                        bottomNavigationView.getMenu().findItem(R.id.nav_dashboard).setIcon(R.drawable.ic_dashboard_outline);
                        bottomNavigationView.getMenu().findItem(R.id.nav_map).setIcon(R.drawable.ic_map_outline);
                        bottomNavigationView.getMenu().findItem(R.id.nav_history).setIcon(R.drawable.ic_history_outline);
                        bottomNavigationView.getMenu().findItem(R.id.nav_settings).setIcon(R.drawable.ic_settings_outline);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                        if (item.getItemId() == R.id.nav_dashboard) {
                            selectedFragment = new DashboardFragment();
                            activeFragmentTag = "DASHBOARD";
                            item.setIcon(R.drawable.ic_dashboard_filled);
                        } else if (item.getItemId() == R.id.nav_map) {
                            selectedFragment = getSupportFragmentManager().findFragmentByTag("MAP");
                            if (selectedFragment == null) {
                                selectedFragment = new MapFragment();
                                transaction.add(R.id.fragment_container, selectedFragment, "MAP");
                            }
                            activeFragmentTag = "MAP";

                            item.setIcon(R.drawable.ic_map_filled);


                        } else if (item.getItemId() == R.id.nav_history) {
                            selectedFragment = new HistoryFragment();
                            activeFragmentTag = "HISTORY";

                            item.setIcon(R.drawable.ic_history_filled);
                        } else if (item.getItemId() == R.id.nav_settings) {
                            selectedFragment = new SettingsFragment();
                            activeFragmentTag = "SETTING";
                            item.setIcon(R.drawable.ic_settings_filled);
                        }
                        if (selectedFragment != null) {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, selectedFragment)
                                    .commit();
                        }
                        return true;
                    }
                });

                FloatingActionButton fab = findViewById(R.id.nav_report);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, ReportActivity.class);
                        startActivity(intent);
                    }
                });


        }
    }


    private void switchFragment(String tag, Fragment fragment) {
        Fragment existingFragment = getSupportFragmentManager().findFragmentByTag(tag);

        if (existingFragment == null) {
            // If the fragment does not exist, create it and add it
            existingFragment = fragment;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, existingFragment, tag)
                    .commit();
        } else {
            // If it already exists, simply show it
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, existingFragment, tag)
                    .commit();
        }

        activeFragmentTag = tag;

//        setLocale(WelcomeActivity.language_static);
    }
    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);



        setIntent(intent);

        if (intent != null && intent.hasExtra("selected_location")) {
            LocationSearch selectedLocation = (LocationSearch) intent.getSerializableExtra("selected_location");
            if (selectedLocation != null) {
                Toast.makeText(this, selectedLocation.getName(), Toast.LENGTH_SHORT).show();
                sendLocationToMapFragment(selectedLocation);
            }
        }

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
        if (type.equals("normal")){
            ApiService.apiService.updateData(new Monitor(userdata.get_id(),date, 0, finish - start, 0)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                }

                @Override
                public void onFailure(Call<String> call, Throwable throwable) {

                }
            });
        }
        else if (type.equals("google")){
            ApiService.apiService.updateData(new Monitor(userSignInWithGoogle.get_id(),date, 0, total, 0)).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                }

                @Override
                public void onFailure(Call<String> call, Throwable throwable) {

                }
            });
        }
        else {
            ApiService.apiService.updateData(new Monitor(userSignInWithFacebook.get_id(),date, 0, total, 0)).enqueue(new Callback<String>() {
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
    private void sendLocationToMapFragment(LocationSearch locationSearch) {
        Fragment selectedFragment = getSupportFragmentManager().findFragmentByTag("MAP");

            SharedViewModel viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
            viewModel.setLocation(locationSearch);

            // Đảm bảo MapFragment được hiển thị nếu nó không phải là fragment hiện tại
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment, "MAP")
                    .commit();
            //Toast.makeText(this, "da gui", Toast.LENGTH_SHORT).show();


        //}
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ACTIVE_FRAGMENT_TAG, activeFragmentTag);
    }
    private void DownloadMap(){
       ApiService.apiService.getMap().enqueue(new Callback<ResponseBody>() {
           @Override
           public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
               if(response.isSuccessful()&& response.body()!=null){
                   boolean success = writeResponseBodyToDisk(response.body());
                   if (success) {
                       Toast.makeText(getApplicationContext(), "Download successful", Toast.LENGTH_SHORT).show();
                   } else {
                       Toast.makeText(getApplicationContext(), "Failed to save map file", Toast.LENGTH_SHORT).show();
                   }
               }else{
                   Toast.makeText(getApplicationContext(), "Failed to download map", Toast.LENGTH_SHORT).show();

               }

           }

           @Override
           public void onFailure(Call<ResponseBody> call, Throwable throwable) {
               throwable.printStackTrace();
               Toast.makeText(getApplicationContext(), "Download failed", Toast.LENGTH_SHORT).show();
           }
       });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body){
        try {
            File mapFile = new File(getApplicationContext().getFilesDir(), "langdaihoc.map");


            InputStream inputStream = null;
            FileOutputStream outputStream = null;

            try{
                byte[] fileReader = new byte[4096];
                long filesize  = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream  = new FileOutputStream(mapFile);

                while(true){
                    int read  = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                }
                outputStream.flush();
                return true;
            }catch (IOException e){
                e.printStackTrace();
                return false;
            }finally {
                if(inputStream != null){
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }

        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }
//    private void setLocale(String lang) {
//        Locale locale = new Locale(lang);
//        Locale.setDefault(locale);
//        Resources resources = getResources();
//        Configuration config = resources.getConfiguration();
//        config.setLocale(locale);
//        DisplayMetrics dm = resources.getDisplayMetrics();
//        resources.updateConfiguration(config, dm);
//
//        Intent intent = new Intent(this, WelcomeActivity.class);
//        startActivity(intent);
//        finish();
//    }
}