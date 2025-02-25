package com.example.project_app.Mapdata;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_app.Adapter.LocationAdapter;
import com.example.project_app.MainActivity;
import com.example.project_app.MapFragment;
import com.example.project_app.R;
import com.example.project_app.data.userModel.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapSearchActivity extends AppCompatActivity implements OnLocationClickListener {
    EditText editlocation;
    RecyclerView recyclerView;
    ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map_search);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        editlocation = findViewById(R.id.edit_location);
        recyclerView = findViewById(R.id.recyclerView);
        back = findViewById(R.id.back_btn);

        editlocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String keyword = charSequence.toString();
                searchLocations(keyword);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
    private void searchLocations(String keyword){
        ApiService.apiService.getLocationSearch(keyword).enqueue(new Callback<List<LocationSearch>>() {
            @Override
            public void onResponse(Call<List<LocationSearch>> call, Response<List<LocationSearch>> response) {
                if (response.isSuccessful()) {
                    List<LocationSearch> locationSearches= response.body();
                    if (locationSearches != null) {
                        // Hiển thị danh sách các địa điểm tìm thấy
                        showLocationsInList(locationSearches);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<LocationSearch>> call, Throwable throwable) {
                throwable.printStackTrace();

            }
        });
    }
    private void showLocationsInList(List<LocationSearch> locationSearches) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LocationAdapter adapter = new LocationAdapter(locationSearches, this);
        recyclerView.setAdapter(adapter);
    }



    @Override
    public void onLocationClick(LocationSearch locationSearch) {
        Intent intent = new Intent(MapSearchActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // Thêm flag này
        intent.putExtra("selected_location", locationSearch);
        startActivity(intent);

    }
}