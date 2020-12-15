package com.example.lab3.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.lab3.Models.ModelAuthenticate;
import com.example.lab3.Models.ModelDatabase;
import com.example.lab3.Models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AuthenticateViewModel extends AndroidViewModel {
    private final ModelDatabase database;
    private final ModelAuthenticate authenticate;

    public AuthenticateViewModel(@NonNull Application application) {
        super(application);

        authenticate = new ModelAuthenticate();
        database = new ModelDatabase();
    }

    public boolean isAuthenticated(){
        return authenticate.isAuthenticated();
    }

    public void googleSignIn(String idToken, OnCompleteListener<AuthResult> callback){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        authenticate.signIn(credential).addOnCompleteListener(callback);
    }

    public void createUser() {
        database.getReference("Users/"+ authenticate.getUserId())
                .child("profileImage")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            UserModel user = authenticate.getUserModel();
                            database.setValue("Users/" + authenticate.getUserId(), user);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }
}
