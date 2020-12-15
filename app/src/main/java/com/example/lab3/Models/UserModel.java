package com.example.lab3.Models;

public class UserModel {
    private String userName, profileImage, email;
    private boolean isGravatar;

    public UserModel() { }

    public UserModel(String userName, String profileImage, String email, boolean isGravatar){
        this.userName = userName;
        this.profileImage = profileImage;
        this.email = email;
        this.isGravatar = isGravatar;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isGravatar() {
        return isGravatar;
    }

    public void setGravatar(boolean isGravatar) {
        this.isGravatar = isGravatar;
    }
}
