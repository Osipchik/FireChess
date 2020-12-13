package com.example.lab3.Models;

public class UserModel {
    public String userName;
    public String profileImage;
    public String email;
    public boolean isFirebase;

    public UserModel(String userName, String profileImage, String email, boolean isFirebase){
        this.userName = userName;
        this.profileImage = profileImage;
        this.email = email;
        this.isFirebase = isFirebase;
    }
}
