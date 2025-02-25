package com.example.project_app.data.userModel;

import java.io.Serializable;

public class UserSignInWithFacebook implements Serializable {
    private String _id;
    private String email;
    private String username;
    private String picture;
    private String dateOfBirth;
    private String phoneNumber;

    private boolean isSharing = true;
    private boolean isAlert = true;

    public boolean isSharing() {
        return isSharing;
    }

    public void setSharing(boolean sharing) {
        isSharing = sharing;
    }

    public boolean isAlert() {
        return isAlert;
    }

    public void setAlert(boolean alert) {
        isAlert = alert;
    }

    public UserSignInWithFacebook(String email, String name, String dateOfBirth, String phoneNumber) {
        this.email = email;
        this.username = name;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getName() {
        return username;
    }

    public void setName(String name) {
        this.username = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
