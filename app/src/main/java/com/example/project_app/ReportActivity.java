package com.example.project_app;

import android.Manifest;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.example.project_app.data.userModel.ApiService;
import com.example.project_app.data.userModel.MapPotholeinfo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.protobuf.Api;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private ImageView choosePhoto;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private File file_image ;
    private TextView tv_province, tv_district, tv_ward, tv_road;
    private TextInputEditText tv_address;
    private Button btn_send;
    private String user_id;
    private String username;
    private AutoCompleteTextView pothole_size;
    private Dialog noti_dialog;

    RelativeLayout clickmap;
    private LatLong selectedLatLong; // Để lưu tọa độ được chọn
    MapView mapView;
    private org.mapsforge.map.layer.overlay.Marker previousMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        choosePhoto = findViewById(R.id.choose_photo);
        clickmap = findViewById(R.id.clickmap);

        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.pothole_size);
        String[] options = {"Small", "Medium", "Large"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, options);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnClickListener(v -> autoCompleteTextView.showDropDown());

        tv_address = findViewById(R.id.address);
        tv_district = findViewById(R.id.district);
        tv_province = findViewById(R.id.province_city);
        tv_road = findViewById(R.id.road);
        tv_ward = findViewById(R.id.ward_commune);
        btn_send = findViewById(R.id.btn_send);
        pothole_size = findViewById(R.id.pothole_size);

        noti_dialog = new Dialog(ReportActivity.this);
        noti_dialog.setContentView(R.layout.dialog);
        noti_dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        noti_dialog.getWindow().setBackgroundDrawable(null);
        ImageView dialogicon = noti_dialog.findViewById(R.id.icon);
        TextView dialogtitle = noti_dialog.findViewById(R.id.title);
        TextView dialogdecript = noti_dialog.findViewById(R.id.descript);

        if (MainActivity.type.equals("normal")){
            user_id = MainActivity.userdata.get_id();
            username = MainActivity.userdata.getUsername();
        }
        else if (MainActivity.type.equals("google")){
            user_id = MainActivity.userSignInWithGoogle.get_id();
            username = MainActivity.userSignInWithGoogle.getUsername();
        }
        else if (MainActivity.type.equals("facebook")){
            user_id = MainActivity.userSignInWithFacebook.get_id();
            username = MainActivity.userSignInWithFacebook.getName();
        }

        choosePhoto.setOnClickListener(v -> showImagePickerOptions());
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            String filePath = getRealPathFromURI(selectedImageUri);
                            if (filePath != null){
                                file_image = new File(filePath);
                            }
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                        getContentResolver(),
                                        selectedImageUri
                                );
                                choosePhoto.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bitmap photo = (Bitmap) result.getData().getExtras().get("data");

                        choosePhoto.setImageBitmap(photo);

                        if (photo != null){
                            file_image = saveBitmapToFile(photo);
                        }
                    }
                }
        );

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalDate today = LocalDate.now();
                int day = today.getDayOfMonth();
                int month = today.getMonthValue();
                int year = today.getYear();
                String month1 = Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                String date = day + " " + month1 + " " + year;
                int level = 0;
                String size = pothole_size.getText().toString();
                if (size.equals("Small")){
                    level = 1;
                }
                else if (size.equals("Medium")){
                    level = 2;
                }
                else if (size.equals("Large")){
                    level = 3;
                }

                MapPotholeinfo mapPotholeinfo = new MapPotholeinfo(username, selectedLatLong.latitude, selectedLatLong.longitude,level ,null ,date,false );
                Gson gson = new Gson();
                String mapPotholeinfoJSON = gson.toJson(mapPotholeinfo);

                Log.e("Data", mapPotholeinfoJSON);

                RequestBody mapPotholeinfoBody = RequestBody.create(
                        MediaType.parse("application/json"), mapPotholeinfoJSON
                );

                RequestBody requestFileImage = RequestBody.create(MediaType.parse("image/*"),file_image);
                MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", file_image.getName(), requestFileImage);

                ApiService.apiService.createPothole(MainActivity.token, user_id, imagePart, mapPotholeinfoBody).enqueue(new Callback<MapPotholeinfo>() {
                    @Override
                    public void onResponse(Call<MapPotholeinfo> call, Response<MapPotholeinfo> response) {
                        if (response.code() == 201){
                            dialogicon.setImageResource(R.drawable.tick_icon);
                            dialogtitle.setText("Pothole Saved!");
                            dialogdecript.setText("Your report has been successfully saved.");
                            noti_dialog.show();
                            new Handler().postDelayed(() -> {
                                noti_dialog.dismiss();
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
                    }

                    @Override
                    public void onFailure(Call<MapPotholeinfo> call, Throwable throwable) {

                    }
                });
            }
        });

        ImageButton backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mapView = findViewById(R.id.MapviewReport);
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
        mapView.setZoomLevelMax((byte) 25);
        mapView.setZoomLevelMin((byte) 16);
        LatLong latLong = new LatLong(10.875169535670919, 106.80072290594345);
        mapView.setCenter(latLong);
        mapView.setZoomLevel((byte) 18);

        clickmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert_map_report();
//                Intent intent = new Intent(ReportActivity.this, report_map.class);
//                startActivity(intent);
            }
        });

    }

    private void showImagePickerOptions() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_image_picker);
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        Button cameraButton = dialog.findViewById(R.id.camera_button);
        Button galleryButton = dialog.findViewById(R.id.gallery_button);

        cameraButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_REQUEST_CODE
                );
            } else {
                openCamera();
            }
            dialog.dismiss();
        });

        galleryButton.setOnClickListener(v -> {
            openGallery();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void alert_map_report() {
        Dialog dialog = new Dialog(this, R.style.CustomDialog);
        LayoutInflater inflater = this.getLayoutInflater();
        View customLayout = inflater.inflate(R.layout.custom_alert_map_report, null);
        dialog.setContentView(customLayout);
        MapView alert_map = customLayout.findViewById(R.id.alert_map);
        Button cancel = customLayout.findViewById(R.id.cancel_map_report);
        Button add = customLayout.findViewById(R.id.add_map_report);
        alert_map.setClickable(true);
        alert_map.getMapScaleBar().setVisible(false);
        alert_map.setBuiltInZoomControls(false);
        AndroidGraphicFactory.createInstance(this.getApplicationContext());
        File mapFile = new File(this.getFilesDir(), "langdaihoc.map");
        TileCache tileCache = AndroidUtil.createTileCache(
                this, "mapcache",
                alert_map.getModel().displayModel.getTileSize(),
                16f,
                //mapView.getModel().frameBufferModel.getOverdrawFactor()
                1.5f
        );
        alert_map.getModel().frameBufferModel.setOverdrawFactor(1.0f);
        MapDataStore mapDataStore = new MapFile(mapFile);
        TileRendererLayer tileRendererLayer = new TileRendererLayer(
                tileCache,
                mapDataStore,
                alert_map.getModel().mapViewPosition,
                false,  // isTransparent: false nếu không cần lớp trong suốt
                true,   // renderLabels: true để hiển thị nhãn
                true,   // cacheLabels: true để lưu cache các nhãn
                AndroidGraphicFactory.INSTANCE
        );
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);
        alert_map.getLayerManager().getLayers().add(tileRendererLayer);
        alert_map.setZoomLevelMax((byte) 25);
        alert_map.setZoomLevelMin((byte) 18);
        LatLong latLong = new LatLong(10.875169535670919, 106.80072290594345);
        alert_map.setCenter(latLong);
        alert_map.setZoomLevel((byte) 18);
        dialog.setCancelable(true);
        dialog.show();
        dialog.getWindow().setLayout(
                (int) getResources().getDimension(R.dimen.alert_dialog_width),
                (int) getResources().getDimension(R.dimen.alert_dialog_height)
        );

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLong centerLatLong = alert_map.getModel().mapViewPosition.getMapPosition().latLong;
                selectedLatLong = centerLatLong;

                // Xóa các marker cũ và thêm marker mới vào MapView chính
                ApiService.apiService.getAddress(MainActivity.token, selectedLatLong.latitude, selectedLatLong.longitude).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        String[] location = response.body().toString().split(",");
                        tv_province.setText(location[location.length-3]);
                        tv_district.setText(location[location.length-4]);
                        tv_ward.setText(location[location.length-5]);
                        tv_road.setText(location[location.length-6]);
                        tv_address.setText(response.body().toString());

                        alert_map.getLayerManager().getLayers().clear();
                        alert_map.getLayerManager().getLayers().add(tileRendererLayer);

                        org.mapsforge.map.layer.overlay.Marker marker = new org.mapsforge.map.layer.overlay.Marker(
                                centerLatLong,
                                AndroidGraphicFactory.convertToBitmap(getDrawable(R.drawable.my_location_red)),
                                0, 0
                        );
                        alert_map.getLayerManager().getLayers().add(marker);

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable throwable) {

                    }
                });

                // Đóng dialog
                dialog.dismiss();

                // Thêm marker lên MapView chính
                addMarkerToMainMap(centerLatLong);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

    }

    private void addMarkerToMainMap(LatLong latLong) {
        // Xóa marker cũ nếu tồn tại
        if (previousMarker != null) {
            mapView.getLayerManager().getLayers().remove(previousMarker);
        }

        // Tạo và thêm marker mới
        org.mapsforge.map.layer.overlay.Marker marker = new org.mapsforge.map.layer.overlay.Marker(
                latLong,
                AndroidGraphicFactory.convertToBitmap(getDrawable(R.drawable.my_location_red)),
                0, 0
        );

        mapView.getLayerManager().getLayers().add(marker);

        // Lưu lại marker mới vào biến previousMarker
        previousMarker = marker;

        // Đặt vị trí trung tâm bản đồ
        mapView.setCenter(latLong);
    }

    private String getRealPathFromURI(Uri uri) {
        String result = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private File saveBitmapToFile(Bitmap bitmap) {
        File file = new File(getExternalFilesDir(null), "temp_image.jpg"); // Đường dẫn tạm thời
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

}