package com.example.lab3.ViewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.lab3.Enums.ChessColor;
import com.example.lab3.Enums.Fields;
import com.example.lab3.Models.ModelAuthenticate;
import com.example.lab3.Models.ModelDatabase;
import com.example.lab3.Models.RoomModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class RoomViewModel extends AndroidViewModel {
    private final MutableLiveData<RoomModel> connected = new MutableLiveData<>();

    private final ModelDatabase database;
    private final ModelAuthenticate authenticate;

    public RoomViewModel(@NonNull Application application) {
        super(application);

        database = new ModelDatabase();
        authenticate = new ModelAuthenticate();
    }

    public LiveData<RoomModel> isConnected() {
        return connected;
    }

    public String getMyId() {
        return authenticate.getUserId();
    }

    public void connectToRoom(String key){
        String roomPath = database.buildPath(new String[]{Fields.Rooms, key});
        database.getReference(roomPath).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot usersSnapshot = snapshot.child(Fields.Users);
                    int count = (int) usersSnapshot.getChildrenCount();
                    if (count < 2 && !usersSnapshot.child(authenticate.getUserId()).exists()) {
                        String path = database.buildPath(new String[]{roomPath, Fields.Users, authenticate.getUserId(), Fields.player});
                        database.setValue(path, ChessColor.BLACK).addOnCompleteListener(i -> {
                            connected.setValue(loadModel(key, snapshot, ChessColor.BLACK));
                        });
                    }
                    else if (count <= 2 && usersSnapshot.child(authenticate.getUserId()).exists()){
                        ChessColor player = usersSnapshot.child(authenticate.getUserId()).child(Fields.player).getValue(ChessColor.class);
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

    private RoomModel loadModel(String key, DataSnapshot snapshot, ChessColor player) {
        return new RoomModel(snapshot.child(Fields.name).getValue(String.class), key, player);
    }

    public void createRoom(String roomName) {
        String key = database.push(Fields.Rooms).substring(1);

        Map<String, Object> values = new HashMap<>();
        values.put(Fields.name, roomName);
        values.put(Fields.finished, false);
        String path = database.buildPath(new String[]{Fields.Users, authenticate.getUserId(), Fields.player});
        values.put(path, ChessColor.WHITE);

        database.updateChild(Fields.Rooms + "/" + key, values)
                .addOnCompleteListener(i -> connected.setValue(new RoomModel(roomName, key, ChessColor.WHITE)));
    }
}
