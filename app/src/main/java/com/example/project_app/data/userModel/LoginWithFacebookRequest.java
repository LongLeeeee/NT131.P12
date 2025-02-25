package com.example.project_app.data.userModel;

public class LoginWithFacebookRequest {
    private String accessToken;

    public LoginWithFacebookRequest(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
