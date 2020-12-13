package com.example.lab3.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.lab3.Enums.Fields;
import com.example.lab3.Models.ModelDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ChessViewModel extends AndroidViewModel {
    private ModelDatabase database;
    public String roomId;

    public ChessViewModel(@NonNull Application application) {
        super(application);

        database = new ModelDatabase();
    }

    public void getUsers() {
        database.getReference(Fields.Rooms + "/" + roomId + "/users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setStatic(){
        database.push(Fields.Statistic);
        Map<String, Object> map = new HashMap<>();
        map.put("winner", "");
        map.put("/score/" + "userId 1", "");
        map.put("/score/" + "userId 2", "");
        String path = database.buildPath(new String[] {Fields.Statistic, roomId});
        database.setValue(path, map);
    }
}
