package com.example.project_app;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.project_app.data.userModel.ApiService;
import com.example.project_app.data.userModel.MapPotholeinfo;
import com.example.project_app.data.userModel.potholeResponseDetail;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailHistory extends AppCompatActivity {
    private String token = MainActivity.token;

    MapView mapView;
    LatLong potholeLocation;
    String id_pothole;
    private ImageView pothole_img;
    private String url = "https://9404-113-161-66-26.ngrok-free.app/";
    private TextView size, name, longitudeD, latitudeD, address, time, date, tv_id;
    String address_detail, time_detail, date_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_history);
        Intent intent = getIntent();
        TextView tvId = findViewById(R.id.tv_id);
        ImageView btnCopy = findViewById(R.id.copy_btn);
        mapView = findViewById(R.id.MapDetail);
        Button back = findViewById(R.id.back_btn);
        id_pothole = intent.getStringExtra("item_id");
        pothole_img = findViewById(R.id.pothole_image);

        size = findViewById(R.id.sizeD);
        name = findViewById(R.id.nameD);
        longitudeD = findViewById(R.id.longitudeD);
        latitudeD = findViewById(R.id.latitudeD);
        address = findViewById(R.id.adressD);
        time = findViewById(R.id.timeD);
        date = findViewById(R.id.dateD);
        tv_id = findViewById(R.id.tv_id);
        address_detail = getIntent().getStringExtra("item_address");
        time_detail = getIntent().getStringExtra("item_time");
        date_detail = getIntent().getStringExtra("item_date");

        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Pothole ID", tvId.getText().toString());
                clipboard.setPrimaryClip(clip);

                Dialog dialog = new Dialog(DetailHistory.this);
                dialog.setContentView(R.layout.dialog);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(null);

                ImageView dialogicon = dialog.findViewById(R.id.icon);
                TextView dialogtitle = dialog.findViewById(R.id.title);
                TextView tvId = findViewById(R.id.tv_id);
                tvId.setText(id_pothole);
                //id_pothole = getIntent().getStringExtra("item_id");
                //tvId.setText(id_pothole);


                dialogicon.setImageResource(R.drawable.tick_icon);
                dialogtitle.setText("Copied to Clipboard");

                dialog.show();

            }

        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mapView.setClickable(true);
        mapView.getMapScaleBar().setVisible(false);
        mapView.setBuiltInZoomControls(false);
        AndroidGraphicFactory.createInstance(this.getApplicationContext());
        File mapFile = new File(this.getFilesDir(), "langdaihoc.map");
        TileCache tileCache = AndroidUtil.createTileCache(
                this, "mapcache",
                mapView.getModel().displayModel.getTileSize(),
                16f,
                //mapView.getModel().frameBufferModel.getOverdrawFactor()
                1.5f
        );
        mapView.getModel().frameBufferModel.setOverdrawFactor(1.0f);
        MapDataStore mapDataStore = new MapFile(mapFile);
        TileRendererLayer tileRendererLayer = new TileRendererLayer(
                tileCache,
                mapDataStore,
                mapView.getModel().mapViewPosition,
                false,  // isTransparent: false nếu không cần lớp trong suốt
                true,   // renderLabels: true để hiển thị nhãn
                true,   // cacheLabels: true để lưu cache các nhãn
                AndroidGraphicFactory.INSTANCE
        );
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);
        mapView.getLayerManager().getLayers().add(tileRendererLayer);
        mapView.setZoomLevelMax((byte)25);
        mapView.setZoomLevelMin((byte)16);

        ApiService.apiService.getPotholesById(id_pothole).enqueue(new Callback<potholeResponseDetail>() {
            @Override
            public void onResponse(Call<potholeResponseDetail> call, Response<potholeResponseDetail> response) {
                if (response.code() == 200 && response.body() != null) {
                    potholeResponseDetail potholeResponse = response.body();

                    // Get the nested Pothole object
                    if (potholeResponse != null && potholeResponse.getPothole() != null) {
                        potholeResponseDetail.Pothole potholeDetails = potholeResponse.getPothole();

                        Glide.with(DetailHistory.this).load(url + potholeResponse.getUrl_image()).into(pothole_img);

                        // Extract latitude and longitude
                        double latitude = potholeDetails.getLatitude();
                        double longitude = potholeDetails.getLongitude();

                        // Create LatLong object
                        LatLong potholeLocation = new LatLong(latitude, longitude);

                        SpannableString spannableString = null;
                        if (potholeResponse.getPothole().getLevel() == 1){
                            String text = "Size: Small";
                            spannableString = new SpannableString(text);

                            spannableString.setSpan(
                                    new ForegroundColorSpan(Color.BLACK), // Màu đen
                                    0,
                                    5,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            );
                            spannableString.setSpan(
                                    new ForegroundColorSpan(Color.parseColor("#14D10A")),
                                    6,
                                    text.length(),
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            );
                        }
                        else if (potholeResponse.getPothole().getLevel() == 2){
                            String text = "Size: Medium";
                            spannableString = new SpannableString(text);

                            spannableString.setSpan(
                                    new ForegroundColorSpan(Color.BLACK), // Màu đen
                                    0,
                                    5,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            );
                            spannableString.setSpan(
                                    new ForegroundColorSpan(Color.parseColor("#FFEB0C")),
                                    6,
                                    text.length(),
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            );
                        }
                        else if (potholeResponse.getPothole().getLevel() == 3){
                            String text = "Size: Large";
                            spannableString = new SpannableString(text);

                            spannableString.setSpan(
                                    new ForegroundColorSpan(Color.BLACK), // Màu đen
                                    0,
                                    5,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            );
                            spannableString.setSpan(
                                    new ForegroundColorSpan(Color.parseColor("#ff0000")),
                                    6,
                                    text.length(),
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            );
                        }
                        size.setText(spannableString);

                        name.setText("Name: " + potholeResponse.getPothole().getUsername());
                        name.setTextColor(Color.BLACK);
                        longitudeD.setText("Longitude: " + longitude);
                        longitudeD.setTextColor(Color.BLACK);
                        latitudeD.setText("Latitude: " + latitude);
                        latitudeD.setTextColor(Color.BLACK);

                        address.setText(address_detail);
                        time.setText(time_detail);
                        date.setText(date_detail);
                        tv_id.setText(potholeResponse.getPothole().get_id());

                        // Display the location as a Toast

                        // Add marker to the map
                        addMarkerToMainMap(potholeLocation);

                        // Center the map and set zoom level
                        mapView.setCenter(potholeLocation);
                        mapView.setZoomLevel((byte) 18);
                    }
                } else {
                    // Handle case where response is not as expected
                    Toast.makeText(DetailHistory.this, "Failed to fetch pothole details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<potholeResponseDetail> call, Throwable t) {

            }
        });

    }
    private void addMarkerToMainMap(LatLong latLong) {
        org.mapsforge.map.layer.overlay.Marker marker = new org.mapsforge.map.layer.overlay.Marker(
                latLong,
                AndroidGraphicFactory.convertToBitmap(getDrawable(R.drawable.my_location_red)),
                0, 0
        );

        mapView.getLayerManager().getLayers().add(marker);
    }
}
