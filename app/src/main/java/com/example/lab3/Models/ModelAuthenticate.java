package com.example.lab3.Models;

import android.net.Uri;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ModelAuthenticate {
    private final FirebaseAuth mAuth;

    public ModelAuthenticate() {
        mAuth = FirebaseAuth.getInstance();
    }

    public String getUserName() {
        return mAuth.getCurrentUser().getDisplayName();
    }

    public String getUserId() {
        return mAuth.getCurrentUser().getUid();
    }

    public String getEmail() {
        return mAuth.getCurrentUser().getEmail();
    }

    public Uri getProfileImage(){
        return mAuth.getCurrentUser().getPhotoUrl();
    }

    public UserModel getUserModel(){
        return new UserModel(getUserName(), getProfileImage().toString(), getEmail(), true);
    }

    public void signOut() {
        mAuth.signOut();
    }

    public void signIn(AuthCredential credential, OnCompleteListener<AuthResult> callback){
        mAuth.signInWithCredential(credential).addOnCompleteListener(callback);
    }

    public boolean isAuthenticated(){
        return mAuth.getCurrentUser() != null;
    }
}
