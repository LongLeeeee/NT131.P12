package com.example.project_app.Mapdata;

import com.example.project_app.data.userModel.MapPotholeinfo;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.layer.overlay.Marker;

public class CustomMarkerPothole extends Marker {
    private String username;
    private int level;
    private String id;
    private MapPotholeinfo mapPotholeinfo;
    private String date;

    public CustomMarkerPothole(LatLong position, Bitmap bitmap, int horizontalOffset, int verticalOffset, MapPotholeinfo mapPotholeinfo) {
        super(position, bitmap, horizontalOffset, verticalOffset);
        this.mapPotholeinfo = mapPotholeinfo;
    }

    public MapPotholeinfo getMapPotholeinfo(){
        return mapPotholeinfo;
    }

    public void  setMapPotholeinfo (MapPotholeinfo mapPotholeinfo){
        this.mapPotholeinfo = mapPotholeinfo;
    }


    public String getUsername() {
        return mapPotholeinfo.getUsername();
    }

    public int getLevel() {
        return mapPotholeinfo.getLevel();
    }

    public String getDate() {
        return mapPotholeinfo.getDate();
    }
    public void setDate(String date) {

        this.date = date;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLevel(int level) {
        this.level = level;
    }
    public  void setId(String id){
        this.id = id;
    }
    public String getId(){
        return mapPotholeinfo.get_id();
    }


}
