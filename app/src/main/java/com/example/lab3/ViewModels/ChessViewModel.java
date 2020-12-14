package com.example.lab3.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.lab3.Enums.ChessColor;
import com.example.lab3.Enums.Fields;
import com.example.lab3.Models.ModelAuthenticate;
import com.example.lab3.Models.ModelDatabase;
import com.example.lab3.Models.Statistic;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChessViewModel extends AndroidViewModel {
    private final MutableLiveData<String> rivalId = new MutableLiveData<>();
    private final ModelDatabase database;

    public String roomId;
    public String myId;
    public String roomName;

    public ChessViewModel(@NonNull Application application) {
        super(application);

        database = new ModelDatabase();
        addUsersListener();
    }

    public LiveData<String> rivalId() {
        return rivalId;
    }

    public DatabaseReference getScoreReference(String userId) {
        String path = database.buildPath(new String[]{Fields.Rooms, roomId, Fields.Users, userId, Fields.score});
        return database.getReference(path);
    }

    public void addUsersListener() {
        String path = database.buildPath(new String[]{Fields.Rooms, roomId, Fields.Users});
        database.getReference(path).addValueEventListener(usersListener);
    }

    private void removeUsersListener() {
        String path = database.buildPath(new String[]{Fields.Rooms, roomId, Fields.Users});
        database.getReference(path).removeEventListener(usersListener);
    }

    private final ValueEventListener usersListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                if (!dataSnapshot.getKey().equals(myId)) {
                    rivalId.setValue(dataSnapshot.getKey());
                    removeUsersListener();
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    public void setStatic(int myScore, int rivalScore, boolean isWinner){
        database.push(Fields.Statistic);
        Statistic model = new Statistic(roomId, roomName, isWinner, rivalId.getValue(), String.valueOf(myScore), String.valueOf(rivalScore));

        String path = database.buildPath(new String[] {Fields.Statistic, myId, new Date().toString()});
        database.setValue(path, model);
    }
}
