package com.example.project_app;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

public class ReportMap extends AppCompatActivity {

    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mapView = findViewById(R.id.MapviewReportExtend);
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
        LatLong latLong = new LatLong(10.875169535670919, 106.80072290594345);
        mapView.setCenter(latLong);
        mapView.setZoomLevel((byte) 18);
    }
}