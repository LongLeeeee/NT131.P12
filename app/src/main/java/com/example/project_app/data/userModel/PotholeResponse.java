package com.example.project_app.data.userModel;

import java.util.List;

public class PotholeResponse {
    private boolean success;
    private List<MapPotholeinfo> potHoles;

    // Getters và Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<MapPotholeinfo> getPotHoles() {
        return potHoles;
    }

    public void setPotHoles(List<MapPotholeinfo> potHoles) {
        this.potHoles = potHoles;
    }
}
