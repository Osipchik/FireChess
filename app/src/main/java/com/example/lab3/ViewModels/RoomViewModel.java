package com.example.lab3.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.lab3.Enums.ChessColor;
import com.example.lab3.Models.ModelAuthenticate;
import com.example.lab3.Models.ModelDatabase;
import com.example.lab3.Models.ModelRoom;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class RoomViewModel extends AndroidViewModel {
    private final MutableLiveData<ModelRoom> connected = new MutableLiveData<>();

    private final ModelDatabase database;
    private final ModelAuthenticate authenticate;

    public RoomViewModel(@NonNull Application application) {
        super(application);

        database = new ModelDatabase();
        authenticate = new ModelAuthenticate();
    }

    public LiveData<ModelRoom> isConnected() {
        return connected;
    }

    public String getMyId() {
        return authenticate.getUserId();
    }

    public void connectToRoom(String key){
        String roomPath = "Rooms/" + key;
        database.getReference(roomPath).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot usersSnapshot = snapshot.child("Users");
                    int count = (int) usersSnapshot.getChildrenCount();
                    if (count < 2 && !usersSnapshot.child(authenticate.getUserId()).exists()) {
                        database.setValue(roomPath +"/Users/" + authenticate.getUserId() + "/player", ChessColor.BLACK).addOnCompleteListener(i -> {
                            connected.setValue(loadModel(key, snapshot, ChessColor.BLACK));
                        });
                    }
                    else if (count <= 2 && usersSnapshot.child(authenticate.getUserId()).exists()){
                        ChessColor player = usersSnapshot.child(authenticate.getUserId()).child("player").getValue(ChessColor.class);
                        connected.setValue(loadModel(key, snapshot, player));
                    }
                    else {
                        connected.setValue(null);
                    }
                }
                else {
                    connected.setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private ModelRoom loadModel(String key, DataSnapshot snapshot, ChessColor player) {
        return new ModelRoom(snapshot.child("name").getValue(String.class), key, player);
    }

    public void createRoom(String roomName) {
        String key = database.push("Rooms").substring(1);

        Map<String, Object> values = new HashMap<>();
        values.put("name", roomName);
        values.put("finished", false);
        values.put("Users/" + authenticate.getUserId() + "/player", ChessColor.WHITE);

        database.updateChild("Rooms/" + key, values)
                .addOnCompleteListener(i -> connected.setValue(new ModelRoom(roomName, key, ChessColor.WHITE)));
    }
}
