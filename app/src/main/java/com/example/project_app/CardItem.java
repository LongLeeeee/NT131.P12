package com.example.project_app;

public class CardItem {
    private String _id;
    private String id_pothole;
    private String address;
    private String date;
    private String size;
    private String time;
    private String status;
    private String url_image;


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public String getUrl_image() {
        return url_image;
    }

    public void setUrl_image(String url_image) {
        this.url_image = url_image;
    }

    public CardItem(String _id, String id_pothole, String address, String date, String size, String time, String status, String url_image) {
        this._id = _id;
        this.id_pothole = id_pothole;
        this.address = address;
        this.date = date;
        this.size = size;
        this.status = status;
        this.time = time;
        this.url_image = url_image;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getId_pothole() {
        return id_pothole;
    }
    public void setId_pothole(String id_pothole) {
        this.id_pothole = id_pothole;
    }
}
