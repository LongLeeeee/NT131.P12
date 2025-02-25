package com.example.project_app.data.userModel;

public class Monitor {
    private String username;
    private String date;
    private int uploadedPothole;
    private long hoursOfUse;
    private double distance;

    public Monitor(String username, String date, int upLoadedPothole, long hoursOfUse, double distance) {
        this.username = username;
        this.date = date;
        this.uploadedPothole = upLoadedPothole;
        this.hoursOfUse = hoursOfUse;
        this.distance = distance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getUpLoadedPothole() {
        return uploadedPothole;
    }

    public void setUpLoadedPothole(int upLoadedPothole) {
        this.uploadedPothole = upLoadedPothole;
    }

    public long getHoursOfUse() {
        return hoursOfUse;
    }

    public void setHoursOfUse(long hoursOfUse) {
        this.hoursOfUse = hoursOfUse;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
