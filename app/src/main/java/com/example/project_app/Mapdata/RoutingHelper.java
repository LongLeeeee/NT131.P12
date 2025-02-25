package com.example.project_app.Mapdata;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.config.Profile;

import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.GHPoint;

import org.mapsforge.core.graphics.Cap;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.overlay.Polyline;

import java.io.File;
import java.util.List;

public class RoutingHelper {
    private GraphHopper hopper;
    public void initializeGraphHopper(File graphHopperFolder, File osmFile) {
        hopper = new GraphHopper()
                .setOSMFile(osmFile.getAbsolutePath()) // Đường dẫn đến file OSM
                .setGraphHopperLocation(graphHopperFolder.getAbsolutePath()) // Thư mục chứa dữ liệu GraphHopper
                .setProfiles(new Profile("car").setWeighting("fastest").setVehicle("car"));


        hopper.importOrLoad();
    }
    public ResponsePath getRoute(double latfrom, double lonfrom, double latto , double lonto){
        GHRequest req = new GHRequest(latfrom, lonfrom, latto, lonto).setProfile("car");
        GHResponse rsp = hopper.route(req);
        if(rsp.hasErrors()){
            for (Throwable error : rsp.getErrors()) {
                System.out.println("Error: " + error.getMessage());
            }
            return null;
        }

        return rsp.getBest();
    }
    public void drawRouteOnMap(MapView mapView, ResponsePath path){
        if(path == null){
            return;
        }
        PointList points = path.getPoints();
        Polyline routeLine = new Polyline(createPaintStroke(), AndroidGraphicFactory.INSTANCE);

        for(int i  = 0; i<points.size(); i++){
            double lat = points.getLat(i);
            double lon = points.getLon(i);
            routeLine.getLatLongs().add(new LatLong(lat, lon));

        }
        Layers layers = mapView.getLayerManager().getLayers();
        layers.add(routeLine);
    }
    private Paint createPaintStroke(){
        Paint paint = AndroidGraphicFactory.INSTANCE.createPaint();
        paint.setStrokeWidth(8);
        paint.setStyle(Style.STROKE);
        paint.setColor(Color.BLUE);
        paint.setStrokeCap(Cap.ROUND);

        return paint;

    }


}
