package com.example.lab3.ViewModels;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.lab3.Enums.Fields;
import com.example.lab3.Models.ModelAuthenticate;
import com.example.lab3.Models.ModelDatabase;
import com.example.lab3.Models.ModelStorage;
import com.example.lab3.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AccountViewModel extends AndroidViewModel {
    private final MutableLiveData<String> userName = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<Uri> imageUri = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFirebase = new MutableLiveData<>();
    private Uri previousImageUri;

    private final MutableLiveData<Integer> messageId = new MutableLiveData<>();

    private final ModelDatabase database;
    private final ModelStorage storage;
    private final ModelAuthenticate authenticate;

    private final String userPath;

    public AccountViewModel(@NonNull Application application) {
        super(application);

        authenticate = new ModelAuthenticate();
        database = new ModelDatabase();
        storage = new ModelStorage();

        userPath = Fields.Users + authenticate.getUserId();
    }

    public void logout(){
        authenticate.signOut();
    }

    public void setName(String name){
        if (userName != null){
            userName.setValue(name);
        }
    }

    public LiveData<String> getUserName() {
        return userName;
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public LiveData<Uri> getImageUri() {
        return imageUri;
    }

    public LiveData<Boolean> getIsFirebase() {
        return isFirebase;
    }

    public LiveData<Integer> getMessageId() {
        return messageId;
    }

    public void setImageUri(Uri Image) {
        previousImageUri = imageUri.getValue();
        imageUri.setValue(Image);
    }

    public void setIsFirebase(boolean b){
        isFirebase.setValue(b);

        if (b){

        }
    }

    public void loadData() {
        ValueEventListener profileListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName.setValue(snapshot.child(Fields.userName).getValue(String.class));
                imageUri.setValue(Uri.parse(snapshot.child(Fields.profileImage).getValue(String.class)));
                email.setValue(snapshot.child(Fields.email).getValue(String.class));
                isFirebase.setValue(snapshot.child(Fields.isFirebase).getValue(Boolean.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        database.getReference(userPath).addValueEventListener(profileListener);
    }

    public void updateProfile() {
        uploadImage();

        Map<String, Object> data = new HashMap<>();
        data.put(Fields.userName, userName.getValue());
        data.put(Fields.isFirebase, isFirebase.getValue());

        updateData(data);
    }

    private void updateData(Map<String, Object> data){
        database.updateChild(userPath, data).addOnCompleteListener(i -> {
            int message = i.isSuccessful() ? R.string.success_update_toast : R.string.failed_update_toast;
            messageId.setValue(message);
        });
    }

    private void updateImage(Uri uri){
        imageUri.setValue(uri);
        previousImageUri = null;

        Map<String, Object> data = new HashMap<>();
        data.put(Fields.profileImage, imageUri.getValue().toString());
        updateData(data);
    }

    private void uploadImage()
    {
        if (previousImageUri != null) {
            final StorageReference saveReference = storage.getReference().child("images/" + UUID.randomUUID().toString());

            saveReference.putFile(imageUri.getValue()).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    messageId.setValue(R.string.failed_upload);
                }

                return saveReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    updateImage(task.getResult());

                    storage.getReferenceFromUrl(previousImageUri.toString()).delete();
                }
            });
        }
    }

    private String hex(byte[] array) {
        StringBuilder sb = new StringBuilder();
        for (byte b : array) {
            sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }

    private String toMd5Hash(String email){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return hex (md.digest(email.getBytes("CP1252")));
        } catch (Exception ignored) { }
        return null;

    }
}
