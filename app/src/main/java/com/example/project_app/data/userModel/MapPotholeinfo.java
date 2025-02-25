package com.example.project_app.data.userModel;

public class MapPotholeinfo {
    private String username;
    private double latitude;
    private double longitude;
    private int level;
    private String id;
    private String _id;
    private String date;
    private Boolean isSharing;

    public String getUsername() {
        return username;
    }

    public MapPotholeinfo(String username, double latitude, double longitude, int level, String _id, String date, Boolean isSharing) {
        this.username = username;
        this.latitude = latitude;
        this.longitude = longitude;
        this.level = level;
        this._id = _id;
        this.date = date;
        this.isSharing = isSharing;

    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public void setIsSharing(Boolean isSharing) {
        this.isSharing = isSharing;
    }
    public Boolean getIsSharing() {
        return isSharing;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
