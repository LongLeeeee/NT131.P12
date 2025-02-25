package com.example.project_app;



import static android.content.ContentValues.TAG;


import android.Manifest;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import org.mapsforge.core.graphics.Paint;

import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_app.Mapdata.CustomMarkerPothole;
import com.example.project_app.Mapdata.MapSearchActivity;
import com.example.project_app.Mapdata.OnMarkerClickListener;
import com.example.project_app.Mapdata.SharedViewModel;
import com.example.project_app.data.userModel.ApiService;
import com.example.project_app.data.userModel.MapPotholeinfo;
import com.example.project_app.data.userModel.Monitor;
import com.example.project_app.data.userModel.PotholeResponse;
import com.example.project_app.data.userModel.RoutingResponse;
import com.example.project_app.data.userModel.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.graphhopper.GraphHopper;

import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Circle;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.ExternalRenderTheme;
import org.mapsforge.map.rendertheme.InternalRenderTheme;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapFragment extends Fragment {

    private String token = MainActivity.token;

    TextView editlocationbtn;
    ImageView map_style, my_location, search_location, close_routing;
    SensorManager sensorManager;
    Sensor accelerometer;
    String usercurrent;
    View bottomSheet;
    View bottomSheetPlace;
    View bottomSheetStart;
    LatLong my_currenlocation;
    List<MapPotholeinfo> potholes = new ArrayList<>();
    List<MapPotholeinfo> potholesById;
    List<LatLong> potholeLocations;
    MapView mapView;
    LocationCallback locationCallback;
    private Marker currentLocationMarker = null;
    private Circle currentLocationCircle = null;
    Marker marker_locationplace;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    String nameicon = "";
    RecyclerView recyclerView;
    FusedLocationProviderClient fusedLocationClient;
    private GraphHopper hopper;
    String startPoint ;
    String endPoint ;
    LatLong desLocation;
    Polyline polyline;
    private Handler handler = new Handler();
    BoundingBox boundingBox;
    int small_pothole_route = 0, medium_pothole_route = 0, big_pothole_route = 0;
    TextView popup_route_number_small;
    TextView popup_route_number_medium;
    TextView popup_route_number_big;
    int bottomSheetPlaceHeight =0;
    int bottomSheetStartHeight = 0;
    CardView alertNearPothole;
    CardView ShowPotholeMain;
    double totalDistance = 0.0;
    LatLong lastLocation = null;

    TextView distanceToPlace;
    TextView timeToPlace;
    RelativeLayout distance_time;

    Boolean isRouting = false;
    LinearLayout direction;
    LinearLayout start;

    TextView disanceToPlaceStart;
    TextView timeToPlaceStart;
    double distanceKm;
    double durationMinutes;
    List<LatLong> latLongList;
    String NamePlace;
    LatLong PositionPlace;
    String user_id = "";
    TextView small_pothole_main;
    TextView medium_pothole_main;
    TextView big_pothole_main;
    LinearLayout FrameAlert;
    Boolean isSharingMap;
    Boolean isAlertMap;
    private int level = 1;
    int Theme = 1;

    public MapFragment() {

    }

    private OnMarkerClickListener onMarkerClickListener;
    public void setOnMarkerClickListener(OnMarkerClickListener listener) {
        this.onMarkerClickListener = listener;
    }
    public final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];
                processAccelerometerData(x, y, z);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    private void processAccelerometerData(float x, float y, float z) {
        // Tính toán gia tốc tổng
        double acceleration = Math.sqrt(x * x + y * y + z * z);

        // Đặt ngưỡng để phát hiện ổ gà (giá trị có thể điều chỉnh theo thực tế)
        double minorThreshold = 20.0f;
        double moderateThreshold = 25.0f;
        double severeThreshold = 30.0f;

        if (acceleration > severeThreshold) {

            getlocation(3);
        } else if (acceleration > moderateThreshold && acceleration < severeThreshold) {

            getlocation(2);
        } else if (acceleration > minorThreshold && acceleration < moderateThreshold) {

            getlocation(1);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (MainActivity.type.equals("normal")) {
            usercurrent = SigninActivity.usernameCurrent;
        }
        if (MainActivity.type.equals("google")) {
            usercurrent = WelcomeActivity.google_name;
        }
        if (MainActivity.type.equals("facebook")) {
            usercurrent = WelcomeActivity.facebook_name;
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        LatLong currentLocation = new LatLong(location.getLatitude(), location.getLongitude());
                        float accuracyRadius = location.getAccuracy(); // Độ chính xác của GPS

                        // Xóa các lớp marker và vòng tròn cũ, thêm lớp mới
                        addCurrentLocationMarker(currentLocation, accuracyRadius);

                        if(potholes!=null){
                            checkNearbyPotholes(currentLocation, potholes, 100); // alertRadius = 50m
                        }



                        if(isRouting && latLongList != null && !latLongList.isEmpty()){
                            mapView.setCenter(currentLocation);
                            mapView.setZoomLevel((byte) 18);
                            onLocationUpdated(currentLocation);
                            //int closestIndex  =findClosestPointIndex(latLongList, currentLocation);
                           // removeOldPoints(latLongList, closestIndex);
                           // redrawRouteOnMap(latLongList);
                            if(polyline!=null && mapView!=null){
                                mapView.getLayerManager().getLayers().remove(polyline);
                                polyline = null;

                            }
                            getCurrentLocatRoute();
                            disanceToPlaceStart.setText(String.format("%.2f km", distanceKm));
                            timeToPlaceStart.setText(String.format("%.2f minutes", durationMinutes));
                            small_pothole_main.setText(small_pothole_route+"");
                            medium_pothole_main.setText(medium_pothole_route+"");
                            big_pothole_main.setText(big_pothole_route+"");
                            small_pothole_route = 0; medium_pothole_route = 0; big_pothole_route = 0;

                                double distance  = calculateDistance(currentLocation, desLocation);
                                if(distance<=20){
                                    isRouting  = false;
                                    if(polyline!=null && mapView!=null){
                                        mapView.getLayerManager().getLayers().remove(polyline);
                                        polyline = null;

                                    }
                                    close_routing.performClick();
                                    hideAlertLayout(ShowPotholeMain);

                                }
                        }
                    }
                }
            }
        };

    }

    private Object getSystemService(String serviceName) {
        return requireActivity().getSystemService(serviceName);
    }







    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        editlocationbtn = view.findViewById(R.id.edit_location_btn);
        map_style = view.findViewById(R.id.Map_Style);
        my_location = view.findViewById(R.id.my_location);
        mapView = view.findViewById(R.id.mapView);
        bottomSheet = view.findViewById(R.id.bottom_sheet);
        bottomSheet.setVisibility(View.GONE);
        bottomSheetPlace = view.findViewById(R.id.bottom_sheet_place);
        bottomSheetStart = view.findViewById(R.id.bottom_sheet_start);
        bottomSheetStart.setVisibility(View.GONE);
        bottomSheetPlace.setVisibility(View.GONE);
        search_location = view.findViewById(R.id.te);
        recyclerView = view.findViewById(R.id.recyclerView);
        close_routing = view.findViewById(R.id.closerouting);
        popup_route_number_small = view.findViewById(R.id.popup_route_number_small);
        popup_route_number_medium = view.findViewById(R.id.popup_route_number_medium);
        popup_route_number_big = view.findViewById(R.id.popup_route_number_big);
        alertNearPothole = view.findViewById(R.id.alertNearPothole);
        distanceToPlace = view.findViewById(R.id.disanceToPlace);
        timeToPlace = view.findViewById(R.id.timeToPlace);
        distance_time = view.findViewById(R.id.distance_time);
        direction  = view.findViewById(R.id.direction);
        start = view.findViewById(R.id.start);
        disanceToPlaceStart = view.findViewById(R.id.disanceToPlaceStart);
        timeToPlaceStart = view.findViewById(R.id.timeToPlaceStart);
        ShowPotholeMain = view.findViewById(R.id.ShowPotholeMain);
        small_pothole_main = view.findViewById(R.id.small_pothole_main);
        medium_pothole_main = view.findViewById(R.id.medium_pothole_main);
        big_pothole_main = view.findViewById(R.id.big_pothole_main);
        FrameAlert = view.findViewById(R.id.frameAlert);
        loadmap(Theme);




        my_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocaton();
            }
        });

        fetchPotholes();
        editlocationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), MapSearchActivity.class);
                startActivity(intent);
            }
        });




        // Sử dụng SharedViewModel để lắng nghe dữ liệu từ Activity (trường hợp Fragment đã tồn tại)
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getLocation().observe(getViewLifecycleOwner(), locationSearch -> {
            if (locationSearch != null) {

               // Toast.makeText(requireContext(), "chay", Toast.LENGTH_SHORT).show();

                    String locationName = locationSearch.getName();
                    NamePlace = locationName;
                    double latitude = locationSearch.getLatitude();
                    double longitude = locationSearch.getLongtitude();
                    LatLong latLong = new LatLong(latitude, longitude);
                    PositionPlace = latLong;

                    endPoint = longitude+","+latitude;


                    addMarkerPlace(latLong,"location_place", locationName);
                    viewModel.setLocation(null);


            }
        });
        viewModel.getIsSharing().observe(getViewLifecycleOwner(), isSharing -> {
            // Cập nhật giao diện khi isSharing thay đổi
            isSharingMap = isSharing;
        });

//        File osmFile = new File(requireActivity().getFilesDir(),"vietnam.osm.pbf");
//        File graphHopperFolder = new File(requireActivity().getFilesDir(), "graphhopper_data");
//        RoutingHelper routingHelper = new RoutingHelper();
//        routingHelper.initializeGraphHopper(graphHopperFolder, osmFile);
//        //10.88208799902974, 106.78268764017908
//       // 10.88067621831339, 106.78436993195433
//
//        double latFrom = 10.88067621831339; // Tọa độ điểm xuất phát
//        double lonFrom = 106.78436993195433;
//        double latTo = 10.88208799902974;
//        double lonTo = 106.78268764017908;
//        ResponsePath path = routingHelper.getRoute(latFrom, lonFrom, latTo, lonTo);
//        if (path != null) {
//            routingHelper.drawRouteOnMap(mapView, path);
//        }
            map_style.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Theme == 1){
                        loadmap(2);
                    }else {
                        loadmap(1);
                    }

                }
            });



            close_routing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(polyline!=null && mapView!=null){
                        mapView.getLayerManager().getLayers().remove(polyline);
                        polyline = null;

                    }
                    mapView.getLayerManager().getLayers().remove(marker_locationplace);
                    marker_locationplace = null;
                    isRouting = false;
                    editlocationbtn.setText("");
                    close_routing.setVisibility(View.INVISIBLE);
                    getCurrentLocaton();
                    closeBottomSheet(bottomSheetPlace, bottomSheetPlaceHeight);
                    closeBottomSheet(bottomSheetStart, bottomSheetStartHeight);
                    hideAlertLayout(ShowPotholeMain);
                    direction.setVisibility(View.VISIBLE);
                    start.setVisibility(View.GONE);
                    //Toast.makeText(requireContext(), totalDistance+"", Toast.LENGTH_SHORT).show();
                    SendTotalDistance();
                    totalDistance = 0.0;
                    small_pothole_route = 0; medium_pothole_route = 0; big_pothole_route = 0;


                }
            });
        alertNearPothole.setVisibility(View.GONE);
        ShowPotholeMain.setVisibility(View.GONE);


        if (MainActivity.type.equals("normal")){
            user_id = MainActivity.userdata.get_id();
        }
        else if(MainActivity.type.equals("google")){
            user_id = MainActivity.userSignInWithGoogle.get_id();
        }
        else if (MainActivity.type.equals("facebook")){
            user_id = MainActivity.userSignInWithFacebook.get_id();
        }


        return view;
    }
    private  void loadmap(int theme){
        mapView.setClickable(true);
        mapView.getMapScaleBar().setVisible(true);
        mapView.setBuiltInZoomControls(true);


        AndroidGraphicFactory.createInstance(requireContext().getApplicationContext());
         /*boundingBox = new BoundingBox(

                10.869043052368955,
                106.77091032419943,
                10.890224555855804,
                106.8211216480134
        );*/
        /*mapView.getModel().mapViewPosition.addObserver(() -> {
            LatLong center = mapView.getModel().mapViewPosition.getCenter();
            if (!boundingBox.contains(center)) {
                // Đặt lại trung tâm của bản đồ về giới hạn gần nhất nếu vượt ra khỏi giới hạn
                double latitude = Math.max(boundingBox.minLatitude, Math.min(boundingBox.maxLatitude, center.latitude));
                double longitude = Math.max(boundingBox.minLongitude, Math.min(boundingBox.maxLongitude, center.longitude));
                mapView.setCenter(new LatLong(latitude, longitude));
            }
        });*/
       // LatLong topLeft = new LatLong(10.903 - 0.02, 106.743 + 0.008);
       // LatLong bottomRight = new LatLong(10.845 + 0.02, 106.834 - 0.008);
        //boundingBox = new BoundingBox(bottomRight.latitude, topLeft.longitude, topLeft.latitude, bottomRight.longitude);
        //mapView.getModel().mapViewPosition.addObserver(this::limitMapPanning);
        copyAssetToInternalStorage("Voluntary V5.xml");
        File themeFile = new File(requireContext().getFilesDir(), "Voluntary V5.xml");
        File mapFile = new File(requireActivity().getFilesDir(), "langdaihoc.map");
        if (!mapFile.exists()) {
            //Xử lý lỗi nếu file không tồn tại

           // Toast.makeText(requireContext(), "Error loading MBTiles file", Toast.LENGTH_SHORT).show();

        }
        TileCache tileCache = AndroidUtil.createTileCache(
                requireContext(), "mapcache",
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

        if(theme == 1) {
            tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);

        } else{
            try {tileRendererLayer.setXmlRenderTheme(new ExternalRenderTheme(themeFile));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }




        mapView.getLayerManager().getLayers().add(tileRendererLayer);
        getCurrentLocaton();
        mapView.setZoomLevelMax((byte)25);
        mapView.setZoomLevelMin((byte)16);
        mapView.setZoomLevel((byte)20);
        // addMarker(new LatLong(10.870523824101978, 106.80200589533338), "warnning_red_2");
    }
    private void limitMapPanning(){
        LatLong currentCenter = mapView.getModel().mapViewPosition.getCenter();
        double latMargin = 0.005;
        double lonMargin = 0.005;

        BoundingBox dynamicBoundingBox = new BoundingBox(
                boundingBox.minLatitude - latMargin,
                boundingBox.minLongitude - lonMargin,
                boundingBox.maxLatitude + latMargin,
                boundingBox.maxLongitude + lonMargin
        );

        if (!dynamicBoundingBox.contains(currentCenter)) {
            double clampedLat = Math.max(dynamicBoundingBox.minLatitude, Math.min(dynamicBoundingBox.maxLatitude, currentCenter.latitude));
            double clampedLon = Math.max(dynamicBoundingBox.minLongitude, Math.min(dynamicBoundingBox.maxLongitude, currentCenter.longitude));
            mapView.getModel().mapViewPosition.setCenter(new LatLong(clampedLat, clampedLon));
        }
    }


    private void handleMarkerClick(LatLong taplatlong) {
        boolean markerClicked = false;
        for (Layer layer : mapView.getLayerManager().getLayers()) {
            if (layer instanceof CustomMarkerPothole) {
                CustomMarkerPothole marker = (CustomMarkerPothole) layer;
                if (marker.getLatLong().distance(taplatlong) < 0.0002) { // Kiểm tra khoảng cách click
                    if (onMarkerClickListener != null) {
                        onMarkerClickListener.onMarkerClick(marker);
                    }

                    showBottomSheet(marker);
                    markerClicked = true;
                    break;

                }
            }
        }
        if (!markerClicked) {
            // Nếu không có marker nào được click, không làm gì cả
        }
    }

    private void getlocation(int level){
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            Toast.makeText(requireContext(), "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_SHORT).show();
            if (MainActivity.type.equals("normal")){
                if(MainActivity.userdata.getIsAlert()){
                    alert_pothol(latitude, longitude, level);
                }
            }
            else if (MainActivity.type.equals("google")){
                if (MainActivity.userSignInWithGoogle.isAlert()){
                    alert_pothol(latitude, longitude, level);
                }
            }
            else if (MainActivity.type.equals("facebook")){
                if (MainActivity.userSignInWithFacebook.isAlert()){
                    alert_pothol(latitude, longitude, level);
                }
            }

        } else {
            Toast.makeText(requireContext(), "Unable to retrieve location. Try moving to an open area.", Toast.LENGTH_SHORT).show();
        }

    }
    private void copyAssetToInternalStorage(String fileName) {
        File outFile = new File(requireContext().getFilesDir(), fileName);
        if (outFile.exists()) {
            // Nếu tệp đã tồn tại, không cần sao chép lại
           // Toast.makeText(requireContext(), "file theme đã tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }
        AssetManager assetManager = requireContext().getAssets();
       // File outFile = new File(requireContext().getFilesDir(), fileName);
        try (InputStream in = assetManager.open(fileName);
             OutputStream out = new FileOutputStream(outFile)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Lỗi sao chép file theme!", Toast.LENGTH_SHORT).show();
        }
    }

    private void alert_pothol(double latitude, double longitude, int lv){
        sensorManager.unregisterListener(sensorEventListener);
        level = lv;

        MapPotholeinfo mapPotholeinfo;
        Dialog dialog = new Dialog(requireContext(), R.style.CustomDialog);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View customLayout = inflater.inflate(R.layout.custom_alert_dialog, null);
        dialog.setContentView(customLayout);
        TextView titleText = customLayout.findViewById(R.id.titleText);
        ImageView level_icon = customLayout.findViewById(R.id.alert_icon);
        Button confirmButton = customLayout.findViewById(R.id.confirmButton);
        Button cancelButton = customLayout.findViewById(R.id.cancel_button);
        SwitchCompat switchCompat = customLayout.findViewById(R.id.switch_remember_me);
        titleText.setText("Detect pothole");

        if (MainActivity.type.equals("normal")){
            switchCompat.setChecked(MainActivity.userdata.getIsSharing());
        }
        else if (MainActivity.type.equals("google")){
            switchCompat.setChecked(MainActivity.userSignInWithGoogle.isSharing());
        }
        else if (MainActivity.type.equals("facebook")){
            switchCompat.setChecked(MainActivity.userSignInWithFacebook.isSharing());
        }

        RadioGroup radioGroup = customLayout.findViewById(R.id.radioGroup);
        if (level == 1) {
            level_icon.setImageResource(R.drawable.warnning_green_2);
            nameicon = "warnning_green_2";
            radioGroup.check(R.id.radio_small);
        } else if (level == 2) {
            level_icon.setImageResource(R.drawable.warnning_yellow_2);
            nameicon = "warnning_yellow_2";
            radioGroup.check(R.id.radio_medium);
        } else if (level == 3) {
            level_icon.setImageResource(R.drawable.warnning_red_2);
            nameicon = "warnning_red_2";
            radioGroup.check(R.id.radio_large);
        }

        // Enable RadioGroup to allow user to change the level
        radioGroup.setEnabled(true);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(true);
        }

        // Handle RadioGroup selection change
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            SwitchCompat switchCompat2 = customLayout.findViewById(R.id.switch_remember_me);

            MapPotholeinfo mapPotholeinfo2;
            if (checkedId == R.id.radio_small) {
                level_icon.setImageResource(R.drawable.warnning_green_2);
                nameicon = "warnning_green_2";
                level = 1; // Gán lại level ở đây
            } else if (checkedId == R.id.radio_medium) {
                level_icon.setImageResource(R.drawable.warnning_yellow_2);
                nameicon = "warnning_yellow_2";
                level = 2; // Gán lại level ở đây
            } else if (checkedId == R.id.radio_large) {
                level_icon.setImageResource(R.drawable.warnning_red_2);
                nameicon = "warnning_red_2";
                level = 3; // Gán lại level ở đây
            }

            Toast.makeText(requireContext(), ""+level, Toast.LENGTH_SHORT).show();
            Boolean is = MainActivity.userdata.getIsSharing();
            isSharingMap = MainActivity.userdata.getIsSharing();
            switchCompat2.setChecked(is);
            switchCompat2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    isSharingMap = isChecked;
                    LocalDate today = LocalDate.now();
                    int day = today.getDayOfMonth();
                    int month = today.getMonthValue();
                    int year = today.getYear();
                    String month1 = Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                    String date = day + " " + month1 + " " + year;
                    LatLong postion  = new LatLong(latitude, longitude);
                    MapPotholeinfo mapPotholeinfo = new MapPotholeinfo(usercurrent,latitude, longitude, level,"", date, isSharingMap);
                    Monitor monitor = new Monitor(user_id, date, 1,0,0);
                    confirmButton.setOnClickListener(view -> {
                        dialog.dismiss();
                        addMarker(postion, nameicon, mapPotholeinfo);
                        String username = usercurrent;
                        sendPotholeData(mapPotholeinfo);
                        updateNumberOfPotholesUploaded(monitor);
                        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

                    });
                }
            });


            LocalDate today = LocalDate.now();
            int day = today.getDayOfMonth();
            int month = today.getMonthValue();
            int year = today.getYear();
            String month1 = Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            String date = day + " " + month1 + " " + year;
            LatLong postion  = new LatLong(latitude, longitude);
            mapPotholeinfo2 = new MapPotholeinfo(usercurrent,latitude, longitude, level,"", date, isSharingMap);
            Monitor monitor = new Monitor(user_id, date, 1,0,0);

            confirmButton.setOnClickListener(view -> {
                dialog.dismiss();
                addMarker(postion, nameicon, mapPotholeinfo2);
                String username = usercurrent;
                sendPotholeData(mapPotholeinfo2);
                updateNumberOfPotholesUploaded(monitor);
                sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

            });





            


        });
        if (MainActivity.type.equals("normal")){
            isSharingMap = MainActivity.userdata.getIsSharing();
        }
        else if (MainActivity.type.equals("google")){
            isSharingMap = MainActivity.userSignInWithGoogle.isSharing();
        }
        else if (MainActivity.type.equals("facebook")){
            isSharingMap = MainActivity.userSignInWithFacebook.isSharing();
        }
        switchCompat.setChecked(isSharingMap);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSharingMap = isChecked;
                LocalDate today = LocalDate.now();
                int day = today.getDayOfMonth();
                int month = today.getMonthValue();
                int year = today.getYear();
                String month1 = Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                String date = day + " " + month1 + " " + year;
                LatLong postion  = new LatLong(latitude, longitude);
                MapPotholeinfo mapPotholeinfo = new MapPotholeinfo(usercurrent,latitude, longitude, level,"", date, isSharingMap);
                Monitor monitor = new Monitor(user_id, date, 1,0,0);
                confirmButton.setOnClickListener(view -> {
                    dialog.dismiss();
                    addMarker(postion, nameicon, mapPotholeinfo);
                    String username = usercurrent;
                    sendPotholeData(mapPotholeinfo);
                    updateNumberOfPotholesUploaded(monitor);
                    sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

                });
            }
        });
        LocalDate today = LocalDate.now();
        int day = today.getDayOfMonth();
        int month = today.getMonthValue();
        int year = today.getYear();
        String month1 = Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        String date = day + " " + month1 + " " + year;
        LatLong postion  = new LatLong(latitude, longitude);
        mapPotholeinfo = new MapPotholeinfo(usercurrent,latitude, longitude, level,"", date, isSharingMap);
        Monitor monitor = new Monitor(user_id, date, 1,0,0);

        confirmButton.setOnClickListener(view -> {
            dialog.dismiss();
            addMarker(postion, nameicon, mapPotholeinfo);
            String username = usercurrent;
            sendPotholeData(mapPotholeinfo);
            updateNumberOfPotholesUploaded(monitor);
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        });
        cancelButton.setOnClickListener(view -> {
            dialog.dismiss();
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        });
        dialog.setCancelable(true);
        dialog.setOnCancelListener(dialogInterface -> {
            // Re-register the sensor listener when the dialog is dismissed
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        });
        dialog.show();
        dialog.getWindow().setLayout(
                (int) getResources().getDimension(R.dimen.alert_dialog_width),
                (int) getResources().getDimension(R.dimen.alert_dialog_height)
        );


    }
    private void sendPotholeData(MapPotholeinfo mapPotholeinfoInput) {
        Gson gson = new Gson();
        String mapPotholeinfoJSON = gson.toJson(mapPotholeinfoInput);

        RequestBody mapPotholeinfoBody = RequestBody.create(
                MediaType.parse("application/json"), mapPotholeinfoJSON
        );

        ApiService.apiService.createPothole(token, user_id, null,mapPotholeinfoBody).enqueue(new Callback<MapPotholeinfo>() {
            @Override
            public void onResponse(Call<MapPotholeinfo> call, Response<MapPotholeinfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireContext(), "Pothole saved successfully", Toast.LENGTH_SHORT).show();
                }
                else if (response.code() == 401){
                    new SessionManager(getContext()).Logout();
                }
                else {
                    Toast.makeText(requireActivity(), "Failed to save pothole", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MapPotholeinfo> call, Throwable throwable) {
                Toast.makeText(requireContext(), "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();

            }

        });
    }
    private void updateNumberOfPotholesUploaded(Monitor monitor){
        ApiService.apiService.updateData(monitor).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {

            }
        });
    }
    private void Routing(){

        ApiService.apiService.getPoint(startPoint, endPoint).enqueue(new Callback<RoutingResponse>() {
            @Override
            public void onResponse(Call<RoutingResponse> call, Response<RoutingResponse> response) {
                if(response.isSuccessful()&& response.body() != null){
                    RoutingResponse routingResponse = response.body();
                    if (routingResponse.getRoutes() != null && !routingResponse.getRoutes().isEmpty()){
                        RoutingResponse.Route route = routingResponse.getRoutes().get(0);
                        RoutingResponse.Geometry geometry = route.getGeometry();
                        double distance = route.getDistance();
                        double duration = route.getDuration();
                        distanceKm = distance / 1000.0;
                        durationMinutes = duration / 60.0;
                        distanceToPlace.setText(String.format("%.2f km", distanceKm));
                        timeToPlace.setText(String.format("%.2f minutes", durationMinutes));

                        List<List<Double>> coordinates = geometry.getCoordinates();
                        latLongList = new ArrayList<>();
                        for (List<Double> point: coordinates){
                            double longitude = point.get(0);
                            double latitude = point.get(1);
                            latLongList.add(new LatLong(latitude, longitude));
                        }
                        if(latLongList != null && !latLongList.isEmpty()){
                            desLocation = latLongList.get(latLongList.size()-1);
                        }
                        drawRoute(latLongList);
                        countPotholesOnRoute(latLongList, potholes, 20);

                        popup_route_number_small.setText(small_pothole_route+"");
                        popup_route_number_medium.setText(medium_pothole_route+"");
                        popup_route_number_big.setText(big_pothole_route+"");

                    }else{
                        Log.e(TAG, "No routes found");
                    }
                }else{
                    Log.e(TAG, "Response was not successful");
                }
            }

            @Override
            public void onFailure(Call<RoutingResponse> call, Throwable throwable) {
                Log.e(TAG, "API call failed",throwable );
            }
        });
    }


    private void drawRoute(List<LatLong> latLongList){
        if(mapView == null) return;


        Paint paint = AndroidGraphicFactory.INSTANCE.createPaint();
        paint.setColor(Color.parseColor("#FF368C8B"));
        paint.setStrokeWidth(15f);
        paint.setStyle(Style.STROKE);

        polyline = new Polyline(paint, AndroidGraphicFactory.INSTANCE);
        polyline.getLatLongs().addAll(latLongList);
        mapView.getLayerManager().getLayers().add(polyline);

    }
    private void countPotholesOnRoute(List<LatLong> latLongList, List<MapPotholeinfo>potholeList, double threshold){
        int count  = 0;
        int count_small = 0;
        int count_medium = 0;
        int count_big = 0;
        for(MapPotholeinfo pothole : potholeList){
            for(int i = 0;i<latLongList.size()- 1; i++){
                LatLong start  = latLongList.get(i);
                LatLong end = latLongList.get(i+1);
                if(isPotholeNearSegment(pothole, start, end , threshold)){
                    if(pothole.getLevel() == 1){
                        count_small++;
                        small_pothole_route++;
                    }else if(pothole.getLevel() == 2){
                        count_medium++;
                        medium_pothole_route++;
                    }else if (pothole.getLevel() == 3){
                        count_big++;
                        big_pothole_route++;
                    }
                    count++;
                    break;
                }
            }
        }
        if(count_small == 0){
            small_pothole_route = 0;
        }
        if(count_medium == 0){
            medium_pothole_route = 0;
        }
        if(count_big == 0){
            big_pothole_route = 0;
        }

    }
    private boolean isPotholeNearSegment(MapPotholeinfo pothole, LatLong start, LatLong end , double threshold){
        double distance  = distanceToSegment(pothole, start, end);
        return distance <=threshold;
    }
    private double distanceToSegment(MapPotholeinfo point , LatLong start, LatLong end){
        double A  = point.getLatitude() - start.latitude;
        double B = point.getLongitude()  - start.longitude;
        double C = end.latitude - start.latitude;
        double D = end.longitude - start.longitude;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = -1;
        if(len_sq != 0){
            param = dot/len_sq;
        }
        double xx, yy;

        if (param < 0) {
            xx = start.latitude;
            yy = start.longitude;
        } else if (param > 1) {
            xx = end.latitude;
            yy = end.longitude;
        } else {
            xx = start.latitude + param * C;
            yy = start.longitude + param * D;
        }
        double dx = point.getLatitude() - xx;
        double dy = point.getLongitude() - yy;

        return Math.sqrt(dx * dx + dy * dy) * 111320; // Đổi ra mét, 1 độ ≈ 111.32 km


    }
    private double CurrentLocationDistanceToSegment(LatLong point , LatLong start, LatLong end){
        double A  = point.getLatitude() - start.latitude;
        double B = point.getLongitude()  - start.longitude;
        double C = end.latitude - start.latitude;
        double D = end.longitude - start.longitude;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = -1;
        if(len_sq != 0){
            param = dot/len_sq;
        }
        double xx, yy;

        if (param < 0) {
            xx = start.latitude;
            yy = start.longitude;
        } else if (param > 1) {
            xx = end.latitude;
            yy = end.longitude;
        } else {
            xx = start.latitude + param * C;
            yy = start.longitude + param * D;
        }
        double dx = point.getLatitude() - xx;
        double dy = point.getLongitude() - yy;

        return Math.sqrt(dx * dx + dy * dy) * 111320; // Đổi ra mét, 1 độ ≈ 111.32 km


    }




    private boolean isFarFromRoute(LatLong currentLocation, List<LatLong>Route , double threshold){
        for(int i  = 0; i<Route.size() - 1; i++){
            LatLong start  = Route.get(i);
            LatLong end = Route.get(i+1);
            double distance  = CurrentLocationDistanceToSegment(currentLocation, start, end);
            if (distance<=threshold){ //van gan duong dinh tuyen
                return false;
            }
        }
        return true;
    }




    private void fetchPotholes() {
        ApiService.apiService.getPotholes(token).enqueue(new Callback<PotholeResponse>() {
            @Override
            public void onResponse(Call<PotholeResponse> call, Response<PotholeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    potholesById = response.body().getPotHoles();

                   // Toast.makeText(requireContext(), potholes.size() + "", Toast.LENGTH_SHORT).show();

                    for (MapPotholeinfo pothole : potholesById) {
                        LatLong position = new LatLong(pothole.getLatitude(), pothole.getLongitude());
                        int level = pothole.getLevel();
                        String id = pothole.get_id();
                        String username = pothole.getUsername();
                        String date = pothole.getDate();
                        String userid = pothole.getId();

                        Boolean isSharing = pothole.getIsSharing();

                        if (MainActivity.type.equals("normal")){
                            if(Objects.equals(userid, MainActivity.userdata.get_id()) || isSharing){

                                potholes.add(pothole);
                                if (level == 1) {
                                    addMarker(position, "warnning_green_2", pothole );
                                } else if (level == 2) {
                                    addMarker(position, "warnning_yellow_2", pothole );
                                } else if (level == 3) {
                                    addMarker(position, "warnning_red_2", pothole );
                                }
                                if (my_currenlocation != null && !potholes.isEmpty()) {
                                    //CheckNearbyPothole(currenlocation, potholes);
                                } else if (my_currenlocation == null) {
                                    Toast.makeText(requireContext(), "Current location is not available.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        else if (MainActivity.type.equals("google")){
                            if(Objects.equals(userid, MainActivity.userSignInWithGoogle.get_id()) || isSharing){

                                potholes.add(pothole);
                                if (level == 1) {
                                    addMarker(position, "warnning_green_2", pothole );
                                } else if (level == 2) {
                                    addMarker(position, "warnning_yellow_2", pothole );
                                } else if (level == 3) {
                                    addMarker(position, "warnning_red_2", pothole );
                                }
                                if (my_currenlocation != null && !potholes.isEmpty()) {
                                    //CheckNearbyPothole(currenlocation, potholes);
                                } else if (my_currenlocation == null) {
                                    Toast.makeText(requireContext(), "Current location is not available.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        else if (MainActivity.type.equals("facebook")){
                            if(Objects.equals(userid, MainActivity.userSignInWithFacebook.get_id()) || isSharing){

                                potholes.add(pothole);
                                if (level == 1) {
                                    addMarker(position, "warnning_green_2", pothole );
                                } else if (level == 2) {
                                    addMarker(position, "warnning_yellow_2", pothole );
                                } else if (level == 3) {
                                    addMarker(position, "warnning_red_2", pothole );
                                }
                                if (my_currenlocation != null && !potholes.isEmpty()) {
                                    //CheckNearbyPothole(currenlocation, potholes);
                                } else if (my_currenlocation == null) {
                                    Toast.makeText(requireContext(), "Current location is not available.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch potholes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PotholeResponse> call, Throwable throwable) {
                Toast.makeText(requireContext(), "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }





    private void getCurrentLocaton() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {

                                double Latitude = location.getLatitude();
                                double Longtitude = location.getLongitude();


                                my_currenlocation = new LatLong(Latitude, Longtitude);
                                LatLong currentlocation = new LatLong(location.getLatitude(), location.getLongitude());

                                mapView.setCenter(currentlocation);
                                mapView.setZoomLevel((byte) 18);

                            } else {
                                Toast.makeText(requireContext(), "Unable to retrieve current location", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
    }
    private void getCurrentLocatRoute() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {

                                double Latitude = location.getLatitude();
                                double Longtitude = location.getLongitude();
                                startPoint = Longtitude+","+Latitude;
                                Routing();

                            } else {


                            }
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
    }



    private void addCurrentLocationMarker (LatLong position, float accuracyRadius) {


        if (currentLocationMarker != null) {
            mapView.getLayerManager().getLayers().remove(currentLocationMarker);
        }
        if (currentLocationCircle != null) {
            mapView.getLayerManager().getLayers().remove(currentLocationCircle);
        }

        // Thêm marker cho vị trí hiện tại
        int resId = getActivity().getResources().getIdentifier("gps_my_location", "drawable", getActivity().getPackageName());
        if (resId != 0) {
            Drawable drawable = getResources().getDrawable(resId);
            org.mapsforge.core.graphics.Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(drawable);
            bitmap.scaleTo(60, 60);

            currentLocationMarker = new Marker(position, bitmap, 0, -bitmap.getHeight() / 2);
            mapView.getLayerManager().getLayers().add(currentLocationMarker);
        }


    }
    private void addMarkerPlace(LatLong position, String iconFilename, String placename){
        int resId = getActivity().getResources().getIdentifier(iconFilename, "drawable", getActivity().getPackageName());
        if (resId == 0) {
            Toast.makeText(requireContext(), "Drawable resource not found", Toast.LENGTH_SHORT).show();
            return;
        }
        Drawable drawable = getResources().getDrawable(resId);
        if (drawable == null) {
            Toast.makeText(requireContext(), "Drawable is null", Toast.LENGTH_SHORT).show();
            return;
        }
        org.mapsforge.core.graphics.Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(drawable);
        //Bitmap scaledBitmap = AndroidGraphicFactory.INSTANCE.createBitmap(60, 60);
        bitmap.scaleTo(80, 80);

        marker_locationplace = new Marker(position, bitmap, 0,-bitmap.getHeight() / 2 );
        mapView.getLayerManager().getLayers().add(marker_locationplace);
        mapView.setCenter(position);
        mapView.setZoomLevel((byte) 18);
        showBottomSheetPlace(position, placename);
        editlocationbtn.setText(placename);
        close_routing.setVisibility(View.VISIBLE);

    }

    @SuppressLint("ClickableViewAccessibility")
    private void addMarker(LatLong position, String iconFilename, MapPotholeinfo mapPotholeinfo) {
        int resId = getActivity().getResources().getIdentifier(iconFilename, "drawable", getActivity().getPackageName());
        if (resId == 0) {
            Toast.makeText(requireContext(), "Drawable resource not found", Toast.LENGTH_SHORT).show();
            return;
        }

        Drawable drawable = getResources().getDrawable(resId);
        if (drawable == null) {
            Toast.makeText(requireContext(), "Drawable is null", Toast.LENGTH_SHORT).show();
            return;
        }

        org.mapsforge.core.graphics.Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(drawable);
        //Bitmap scaledBitmap = AndroidGraphicFactory.INSTANCE.createBitmap(60, 60);
        bitmap.scaleTo(60, 60);

        CustomMarkerPothole marker = new CustomMarkerPothole(position, bitmap, 0, -bitmap.getHeight() / 2,mapPotholeinfo);


        // Kiểm tra marker đã được thêm thành công hay chưa
        if (marker != null) {
            //Toast.makeText(requireContext(), "Marker added successfully", Toast.LENGTH_SHORT).show();
            mapView.getLayerManager().getLayers().add(marker);
            mapView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                        LatLong Taplatlong = mapView.getMapViewProjection().fromPixels((int) motionEvent.getX(), (int) motionEvent.getY());
                        handleMarkerClick(Taplatlong);
                        view.performClick();
                        return true;

                    }


                    return false;
                }
            });
        } else {
            Toast.makeText(requireContext(), "Failed to add marker", Toast.LENGTH_SHORT).show();
        }



    }
    @Override
    public void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.destroyAll();

    }

    @Override
    public void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            // Yêu cầu quyền nếu chưa được cấp
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1200); // 2 giây
        locationRequest.setFastestInterval(1200); // 2 giây

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }


    @Override
    public void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
    private double calculateDistance(LatLong point1,LatLong point2 ){
        // su dung cong thuc Haversine
        double earthRadius = 6371e3;
        double lat1 = Math.toRadians(point1.latitude);
        double lat2 = Math.toRadians(point2.latitude);
        double deltaLat = Math.toRadians(point2.latitude - point1.latitude);
        double deltaLon = Math.toRadians(point2.longitude - point1.longitude);
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c; // trả về khoảng cách tính bằng mét
    }
    private void checkNearbyPotholes(LatLong currentLocation, List<MapPotholeinfo> potholeLocations, float alertRadius) {

        if (potholeLocations == null || potholeLocations.isEmpty()) {
            return;
        }
        if(potholeLocations != null){
            for (MapPotholeinfo pothole : potholeLocations) {
                LatLong latLong = new LatLong(pothole.getLatitude(), pothole.getLongitude());
                double distance = calculateDistance(currentLocation, latLong);

                if (distance <= alertRadius) {
                    showAlertLayout(alertNearPothole); // Hiển thị thông báo nếu gần ổ gà
                    return;
                }
            }
            hideAlertLayout(alertNearPothole);
        }

    }
    private void showAlertNotification(String message) {
        View rootView = getActivity().findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);
        snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
        snackbar.show();
    }
    private void showAlertLayout(CardView alert) {
        // Lấy view từ layout

        // Đảm bảo rằng layout đang ở trạng thái INVISIBLE trước khi hiển thị
        if (alert.getVisibility() != View.VISIBLE) {
            alert.setVisibility(View.VISIBLE);

            // Đặt trạng thái ban đầu (scale nhỏ)
            alert.setAlpha(0f);
            alert.setScaleX(0.5f);
            alert.setScaleY(0.5f);

            // Animation Zoom In
            alert.animate()
                    .alpha(1f)                // Tăng độ mờ
                    .scaleX(1f)               // Phóng to theo chiều ngang
                    .scaleY(1f)               // Phóng to theo chiều dọc
                    .setDuration(500)         // Thời gian animation
                    .start();
        }
    }
    private void hideAlertLayout(CardView alert) {

        // Kiểm tra nếu layout đang hiển thị
        if (alert.getVisibility() == View.VISIBLE) {
            alert.animate()
                    .alpha(0f)                // Giảm độ mờ
                    .scaleX(0.5f)             // Thu nhỏ theo chiều ngang
                    .scaleY(0.5f)             // Thu nhỏ theo chiều dọc
                    .setDuration(500)         // Thời gian animation
                    .withEndAction(() -> {
                        alert.setVisibility(View.INVISIBLE); // Ẩn sau khi hoàn tất
                    })
                    .start();
        }
    }

    public void onLocationUpdated (LatLong currentLocation){
        if(lastLocation!=null){
            totalDistance += calculateDistance(lastLocation, currentLocation);
        }
        lastLocation = currentLocation;
    }

    public  void SendTotalDistance(){

        String name  = user_id;
        LocalDate today = LocalDate.now();
        int day = today.getDayOfMonth();
        int month = today.getMonthValue();
        int year = today.getYear();
        String month1 = Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        String date = day + " " + month1 + " " + year;

        ApiService.apiService.updateData(new Monitor(name,date, 0, 0, totalDistance)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Toast.makeText(getContext(), "Data sent successfully!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showBottomSheetStart(LatLong position, String placename){
        TextView Placename = requireView().findViewById(R.id.place_name_start);
        TextView placePosition  = requireView().findViewById(R.id.place_latLong_start);
        Placename.setText(placename);
        placePosition.setText(position.getLatitude()+","+position.getLongitude());

        bottomSheetStart.setVisibility(View.VISIBLE);
        bottomSheetStartHeight = bottomSheetStart.getMeasuredHeight();
        bottomSheetStart.setTranslationY(bottomSheetStartHeight);
        ValueAnimator animator = ValueAnimator.ofFloat(bottomSheetStartHeight, 0);
        animator.setDuration(300); // Duration in milliseconds
        animator.addUpdateListener(animation -> {
            float translationY = (float) animation.getAnimatedValue();
            bottomSheetStart.setTranslationY(translationY);
        });
        animator.start();


    }
    private void showBottomSheetPlace(LatLong position, String placename){
        TextView Placename = requireView().findViewById(R.id.place_name);
        TextView placePosition = requireView().findViewById(R.id.place_latLong);

        CardView direction_btn = requireView().findViewById(R.id.direction_btn);
        CardView close = requireView().findViewById(R.id.close_popup_place);
        CardView discribe_route = requireView().findViewById(R.id.status_route);
        ImageView direction_icon = requireView().findViewById(R.id.direction_icon);

        discribe_route.setVisibility(View.GONE);
        distance_time.setVisibility(View.GONE);
        Placename.setText(placename);
        placePosition.setText(position.getLatitude()+","+position.getLongitude());
        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocatRoute();

                distance_time.setVisibility(View.VISIBLE);
                discribe_route.setVisibility(View.VISIBLE);
                direction.setVisibility(View.GONE);
                start.setVisibility(View.VISIBLE);

            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // direction_btn.setVisibility(View.GONE);
                disanceToPlaceStart.setText(String.format("%.2f km", distanceKm));
                timeToPlaceStart.setText(String.format("%.2f minutes", durationMinutes));
                closeBottomSheet(bottomSheetPlace, bottomSheetPlaceHeight);
                showBottomSheetStart(position, placename);
                getCurrentLocaton();
                showAlertLayout(ShowPotholeMain);
                isRouting = true;
            }
        });


        bottomSheetPlace.setVisibility(View.VISIBLE);
        bottomSheetPlaceHeight = bottomSheetPlace.getMeasuredHeight();
        bottomSheetPlace.setTranslationY(bottomSheetPlaceHeight);
        ValueAnimator animator = ValueAnimator.ofFloat(bottomSheetPlaceHeight, 0);
        animator.setDuration(300); // Duration in milliseconds
        animator.addUpdateListener(animation -> {
            float translationY = (float) animation.getAnimatedValue();
            bottomSheetPlace.setTranslationY(translationY);
        });
        animator.start();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Thêm hiệu ứng tuột xuống khi đóng Bottom Sheet
                ValueAnimator closeAnimator = ValueAnimator.ofFloat(0, bottomSheetPlaceHeight);
                closeAnimator.setDuration(300); // Duration in milliseconds
                closeAnimator.addUpdateListener(animation -> {
                    float translationY = (float) animation.getAnimatedValue();
                    bottomSheetPlace.setTranslationY(translationY);
                });
                closeAnimator.addListener(new android.animation.AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(android.animation.Animator animation) {
                        bottomSheetPlace.setVisibility(View.GONE);
                    }
                });

                closeAnimator.start();
            }
        });


    }
    private void closeBottomSheet (View bottomSheetPlace, int bottomSheetPlaceHeight){
        ValueAnimator closeAnimator = ValueAnimator.ofFloat(0, bottomSheetPlaceHeight);
        closeAnimator.setDuration(300);
        closeAnimator.addUpdateListener(animation -> {
            float translationY = (float) animation.getAnimatedValue();
            bottomSheetPlace.setTranslationY(translationY);
        });
        closeAnimator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                bottomSheetPlace.setVisibility(View.GONE);
            }
        });


//        if (polyline != null && mapView != null) {
//            mapView.getLayerManager().getLayers().remove(polyline);
//            polyline = null;
//        }
//
//        if (marker_locationplace != null && mapView != null) {
//            mapView.getLayerManager().getLayers().remove(marker_locationplace);
//            marker_locationplace = null;
//        }

        closeAnimator.start();
    }


    private void showBottomSheet(CustomMarkerPothole marker) {


        /*BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_popup_info_pothole, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.setCancelable(true);
        // Disable swipe to dismiss
        bottomSheetDialog.setOnShowListener(dialog -> {
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setDraggable(false); // Prevents swiping down to dismiss
            }
        });*/
        TextView username = requireView().findViewById(R.id.username_popup);
        TextView date = requireView().findViewById(R.id.date_upload);
        TextView location = requireView().findViewById(R.id.loacation_popup);
        TextView level = requireView().findViewById(R.id.level_popup);
        ImageView iconLevel = requireView().findViewById(R.id.icon_popup_bottom);
        TextView idpothole = requireView().findViewById(R.id.id_pothole_popup);
        CardView close = requireView().findViewById(R.id.close_popup);
        username.setText(marker.getUsername());
        date.setText(marker.getDate());
        double getLatitude = marker.getPosition().latitude;
        double getLongtitude = marker.getPosition().longitude;
        idpothole.setText(marker.getId());
        location.setText(getLatitude + ", " + getLongtitude);
        if (marker.getLevel() == 1) {
            level.setText("Small pothole");
            iconLevel.setImageResource(R.drawable.warnning_green_2);
        } else if (marker.getLevel() == 2) {
            level.setText("Medium pothole");
            iconLevel.setImageResource(R.drawable.warnning_yellow_2);
        } else if (marker.getLevel() == 3) {
            level.setText("Big pothole");
            iconLevel.setImageResource(R.drawable.warnning_red_2);
        }
        bottomSheet.setVisibility(View.VISIBLE);
        int bottomSheetHeight = bottomSheet.getMeasuredHeight();
        bottomSheet.setTranslationY(bottomSheetHeight);
        ValueAnimator animator = ValueAnimator.ofFloat(bottomSheetHeight, 0);
        animator.setDuration(300); // Duration in milliseconds
        animator.addUpdateListener(animation -> {
            float translationY = (float) animation.getAnimatedValue();
            bottomSheet.setTranslationY(translationY);
        });
        animator.start();
        //bottomSheet.setVisibility(View.VISIBLE);
        // bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Thêm hiệu ứng tuột xuống khi đóng Bottom Sheet
                ValueAnimator closeAnimator = ValueAnimator.ofFloat(0, bottomSheetHeight);
                closeAnimator.setDuration(300); // Duration in milliseconds
                closeAnimator.addUpdateListener(animation -> {
                    float translationY = (float) animation.getAnimatedValue();
                    bottomSheet.setTranslationY(translationY);
                });
                closeAnimator.addListener(new android.animation.AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(android.animation.Animator animation) {
                        bottomSheet.setVisibility(View.GONE);
                    }
                });
                closeAnimator.start();




            }
        });


    }


    private BitmapDescriptor getResizedBitmapDescriptor(int imageResId, int width, int height){
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), imageResId).copy(Bitmap.Config.ARGB_8888, true);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, true);
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap);
    }
}
