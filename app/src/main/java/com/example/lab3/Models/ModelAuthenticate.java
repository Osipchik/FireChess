package com.example.lab3.Models;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
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
        return new UserModel(getUserName(), getProfileImage().toString(), getEmail(), false);
    }

    public void signOut() {
        mAuth.signOut();
    }

    public Task<AuthResult> signIn(AuthCredential credential){
        return mAuth.signInWithCredential(credential);
    }

    public boolean isAuthenticated(){
        return mAuth.getCurrentUser() != null;
    }
}
