package com.example.lab3.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.lab3.Models.ModelDatabase;
import com.example.lab3.Models.StatisticItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ChessViewModel extends AndroidViewModel {
    private final MutableLiveData<String> rivalId = new MutableLiveData<>();
    private final ModelDatabase database;

    private String roomUsersPath;
    private String roomId;
    public String myId;
    public String roomName;

    public ChessViewModel(@NonNull Application application) {
        super(application);

        database = new ModelDatabase();
    }

    public LiveData<String> rivalId() {
        return rivalId;
    }

    public DatabaseReference getScoreReference(String userId) {
        return database.getReference(roomUsersPath + "/" + userId + "/score");
    }

    public void addUsersListener() {
        database.getReference(roomUsersPath).addValueEventListener(usersListener);
    }

    private void removeUsersListener() {
        database.getReference(roomUsersPath).removeEventListener(usersListener);
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
        String key = database.push("Statistic");
        StatisticItem model = new StatisticItem(roomId, roomName, isWinner, rivalId.getValue(), String.valueOf(myScore), String.valueOf(rivalScore));
        database.setValue("Statistic/" + myId + "/" + key, model);
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
        roomUsersPath = "Rooms/" + roomId + "/Users";
    }
}
