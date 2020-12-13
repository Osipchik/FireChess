package com.example.lab3.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.lab3.Enums.ChessColor;
import com.example.lab3.Enums.ChessRank;
import com.example.lab3.Enums.Fields;
import com.example.lab3.Models.ChessItem;
import com.example.lab3.Models.ModelAuthenticate;
import com.example.lab3.Models.ModelDatabase;
import com.example.lab3.Models.ModelItems;
import com.example.lab3.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GameViewModel extends AndroidViewModel {
    private final MutableLiveData<ChessColor> finish = new MutableLiveData<>();
    private final MutableLiveData<Integer> messageId = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateView = new MutableLiveData<>();
    private final ModelDatabase database;
    private final ModelItems items;

    private ChessColor player;
    private Boolean isLocked;

    public String roomPath;


    public GameViewModel(@NonNull Application application) {
        super(application);

        items = new ModelItems();

        database = new ModelDatabase();
        isLocked = true;
    }

    public ChessColor getPlayer(){
        return player;
    }

    public LiveData<Boolean> updateView() {
        return updateView;
    }

    public ChessItem getItemAt(int col, int row){
        return items.getItemAt(col, row);
    }

    public void removeItem(ChessItem item) {
        if (!isLocked) {
            String path = database.buildPath(new String[]{roomPath, Fields.game, Fields.remove});
            database.setValue(path, item.getMap());

            ModelAuthenticate authenticate = new ModelAuthenticate();
            path = database.buildPath(new String[]{roomPath, Fields.Users, authenticate.getUserId(), (new Date().toString())});
//            database.setValue(path, item.getMap());

            database.updateChild(path, item.getMap());
        }
    }

    public void moveItem(ChessItem item, int col, int row) {
        if (!isLocked) {
            setRound();

            Map<String, Object> map = new HashMap<>();
            map.put(Fields.fromCol, item.col);
            map.put(Fields.fromRow, item.row);
            map.put(Fields.toCol, col);
            map.put(Fields.toRow, row);
            String path = database.buildPath(new String[]{roomPath, Fields.game, Fields.move});
            database.setValue(path, map);

            item.row = row;
            item.col = col;
        }
        else {
            messageId.setValue(R.string.waiting_round);
        }
    }

    private void setRound(){
        isLocked = true;
        String path = database.buildPath(new String[]{roomPath, Fields.game, Fields.round});
        database.setValue(path, player);
    }

    public LiveData<ChessColor> getFinish() {
        return finish;
    }

    public LiveData<Integer> getMessageId() {
        return messageId;
    }

    public void saveGame() {
        if (player == ChessColor.WHITE) {
            ArrayList<Map<String, Object>> arr = new ArrayList<>();

            for(ChessItem i : items.getItems()){
                arr.add(i.getMap());
            }

            String path = database.buildPath(new String[]{roomPath, Fields.game, Fields.state});
            database.setValue(path, arr).addOnCompleteListener(i -> messageId.setValue(R.string.game_saved));
        }
    }

    public void init(ChessColor player) {
        this.player = player;
        loadLasGame();
        loadRound();
    }

    private void loadLasGame() {
        String path = database.buildPath(new String[]{roomPath, Fields.game});
        database.getReference(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadField(snapshot.child(Fields.state));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void loadField(DataSnapshot snapshot){
        ArrayList<ChessItem> piecesBox = new ArrayList<>();

        if (snapshot.exists()) {
            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                piecesBox.add(new ChessItem(dataSnapshot.child(Fields.col).getValue(int.class),
                        dataSnapshot.child(Fields.row).getValue(int.class),
                        dataSnapshot.child(Fields.player).getValue(ChessColor.class),
                        dataSnapshot.child(Fields.rank).getValue(ChessRank.class)));
            }
        }
        else {
            piecesBox = items.createDefault();
        }

        items.setItems(piecesBox);
        updateView.setValue(true);

        moveListening();
        removeListening();
    }

    private void loadRound() {
        String path = database.buildPath(new String[]{roomPath, Fields.game, Fields.round});
        database.getReference(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    ChessColor p = snapshot.getValue(ChessColor.class);
                    isLocked = player == p;
                }
                else {
                    isLocked = player != ChessColor.WHITE;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void moveListening() {
        String path = database.buildPath(new String[]{roomPath, Fields.game, Fields.move});
        database.getReference(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int fromCol = snapshot.child(Fields.fromCol).getValue(int.class);
                    int fromRow = snapshot.child(Fields.fromRow).getValue(int.class);

                    ChessItem item = items.getItemAt(fromCol, fromRow);
                    if (item != null) {
                        item.row = snapshot.child(Fields.toRow).getValue(int.class);
                        item.col = snapshot.child(Fields.toCol).getValue(int.class);
                        updateView.setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void removeListening() {
        String path = database.buildPath(new String[]{roomPath, Fields.game, Fields.remove});
        database.getReference(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int fromCol = snapshot.child(Fields.col).getValue(int.class);
                    int fromRow = snapshot.child(Fields.row).getValue(int.class);
                    ChessColor color = snapshot.child(Fields.player).getValue(ChessColor.class);

                    ChessItem item = items.getItemAt(fromCol, fromRow, color);
                    if (item != null) {
                        items.removeItem(item);
                        updateView.setValue(true);

                        if (item.rank == ChessRank.KING) {
                            setFinish(item);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void setFinish(ChessItem item) {
        finish.setValue(item.player == ChessColor.BLACK ? ChessColor.BLACK : ChessColor.WHITE);
        Map<String, Object> values = new HashMap<>();
        values.put(Fields.finished, true);

        database.updateChild(roomPath, values);
    }
}
