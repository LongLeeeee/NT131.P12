package com.example.project_app.data.userModel;

public class LoginWithFacebookResponse {
    private boolean success;
    private String type;
    private UserSignInWithFacebook userInfo;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public UserSignInWithFacebook getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserSignInWithFacebook userInfo) {
        this.userInfo = userInfo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
