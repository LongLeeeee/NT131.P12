package com.example.project_app.Mapdata;

import java.io.Serializable;

public class LocationSearch implements Serializable {
    String name;
    float latitude;
    float longitude;

    public LocationSearch(String name, float latitude, float longitude){
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;

    }

    public String getName(){
        return name;
    }
    public  float getLatitude(){
        return latitude;
    }
    public float getLongtitude(){
        return longitude;
    }
    public void setName(String name){
        this.name = name;
    }
    public  void setLatitude(float latitude){
        this.latitude = latitude;
    }
    public void setLongtitude(float longitude){
        this.longitude = longitude;
    }

}
