package com.example.project_app.data.userModel;

public class UpdatePreferencesRequest {
    private String _id;
    private boolean isSharing;
    private boolean isAlert;

    // Constructor
    public UpdatePreferencesRequest(String _id, boolean isSharing, boolean isAlert) {
        this._id = _id;
        this.isSharing = isSharing;
        this.isAlert = isAlert;
    }

    // Getters và Setters
    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

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
}
