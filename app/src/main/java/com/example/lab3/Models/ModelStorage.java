package com.example.lab3.Models;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ModelStorage {
    private final FirebaseStorage firebaseStorage;

    public ModelStorage() {
        firebaseStorage = FirebaseStorage.getInstance();
    }

    public StorageReference getReference() { return firebaseStorage.getReference(); }

    public StorageReference getReferenceFromUrl(String url) {
        return firebaseStorage.getReferenceFromUrl(url);
    }

    public StorageReference getReference(String path) {
        return firebaseStorage.getReference(path);
    }
}
