package com.example.project_app.data.userModel;

import java.io.Serializable;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class NormalUser implements Serializable {

    private String _id;
    private String username;
    private String password;
    private String email;
    private String dateOfBirth;
    private String phoneNumber;



    private boolean isSharing = true; // Thêm trường isSharing
    private boolean isAlert = true;   // Thêm trường isAlert

    // Getters và Setters cho isSharing
    public boolean getIsSharing() {
        return isSharing;
    }

    public void setIsSharing(boolean isSharing) {
        this.isSharing = isSharing;
    }

    // Getters và Setters cho isAlert
    public boolean getIsAlert() {
        return isAlert;
    }

    public void setIsAlert(boolean isAlert) {
        this.isAlert = isAlert;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}