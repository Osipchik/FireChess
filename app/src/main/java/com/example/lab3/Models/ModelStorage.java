package com.example.lab3.Models;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ModelStorage {
    private final FirebaseStorage firebaseStorage;

    public ModelStorage() {
        firebaseStorage = FirebaseStorage.getInstance();
    }

    public StorageReference getReference() { return firebaseStorage.getReference(); }

    public StorageReference getReferenceFromUrl(Uri url) {
        return firebaseStorage.getReferenceFromUrl(url.toString());
    }

    public Task<Uri> getDownloadUrl(String url) {
        return firebaseStorage.getReferenceFromUrl(url).getDownloadUrl();
    }

    public StorageReference getReference(String path) {
        return firebaseStorage.getReference(path);
    }
}
