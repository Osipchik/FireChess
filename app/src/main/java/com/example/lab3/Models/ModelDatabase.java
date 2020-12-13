package com.example.lab3.Models;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class ModelDatabase {
    private final FirebaseDatabase database;

    public ModelDatabase() {
        database = FirebaseDatabase.getInstance();
    }

    public Task<Void> updateChild(String path, Map<String, Object> values) {
        return database.getReference(path).updateChildren(values);
    }

    public Task<Void> setValue(String path, Object value) {
        return database.getReference(path).setValue(value);
    }

    public DatabaseReference getReference(String path) {
        return database.getReference(path);
    }

    public String push(String path) {
        return database.getReference(path).push().getKey();
    }

    public void remove(String path) {
        database.getReference(path).removeValue();
    }

    public String buildPath(String[] chains){
        StringBuilder path = new StringBuilder();
        for(String i : chains) {
            path.append("/").append(i);
        }

        return path.toString();
    }
}
