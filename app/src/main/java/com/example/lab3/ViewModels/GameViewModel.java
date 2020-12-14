package com.example.lab3.ViewModels;

import android.app.Application;
import android.util.Log;

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
    private String roomPath;
    private boolean isFinished;

    public GameViewModel(@NonNull Application application) {
        super(application);

        items = new ModelItems();

        database = new ModelDatabase();
        isLocked = true;
    }

    public boolean getIsFinished() {
        return isFinished;
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
            database.setValue(path, item);

            ModelAuthenticate authenticate = new ModelAuthenticate();
            path = database.buildPath(new String[]{roomPath, Fields.Users, authenticate.getUserId(), Fields.score, (new Date().toString())});

            database.setValue(path, item);
        }
    }

    public void moveItem(ChessItem item, int col, int row, boolean updateRound) {
        if (!isLocked) {
            if (updateRound) {
                updateRound();
            }

            Map<String, Object> map = new HashMap<>();
            map.put(Fields.fromCol, item.getCol());
            map.put(Fields.fromRow, item.getRow());
            map.put(Fields.toCol, col);
            map.put(Fields.toRow, row);
            String path = database.buildPath(new String[]{roomPath, Fields.game, Fields.move});
            database.setValue(path, map);

            item.setRow(row);
            item.setCol(col);;
        }
        else {
            messageId.setValue(R.string.waiting_round);
        }
    }

    public void updateRound(){
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
            String path = database.buildPath(new String[]{roomPath, Fields.game, Fields.state});
            database.setValue(path, items.getItems());
        }
    }

    public void init(ChessColor player, String roomPath) {
        this.player = player;
        this.roomPath = roomPath;

        loadLasGame();
        loadRound();
    }

    private void loadLasGame() {
        database.getReference(roomPath).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isFinished = snapshot.child(Fields.finished).getValue(boolean.class);
                loadField(snapshot.child(Fields.game).child(Fields.state));
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

        if (!isFinished) {
            moveItemListening();
            removeItemListening();
            promotionListening();
        }
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

    private void moveItemListening() {
        String path = database.buildPath(new String[]{roomPath, Fields.game, Fields.move});
        database.getReference(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int fromCol = snapshot.child(Fields.fromCol).getValue(int.class);
                    int fromRow = snapshot.child(Fields.fromRow).getValue(int.class);

                    ChessItem item = items.getItemAt(fromCol, fromRow);
                    if (item != null) {
                        item.setRow(snapshot.child(Fields.toRow).getValue(int.class));
                        item.setCol(snapshot.child(Fields.toCol).getValue(int.class));
                        updateView.setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void removeItemListening() {
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

                        if (item.getRank() == ChessRank.KING) {
                            isLocked = true;
                            setFinish(item.getPlayer());
                            isFinished = true;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void promotionListening() {
        String path = database.buildPath(new String[]{roomPath, Fields.game, Fields.promotion});
        database.getReference(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ChessItem promotion = snapshot.getValue(ChessItem.class);
                    ChessItem item = getItemAt(promotion.getCol(), promotion.getRow());
                    toPromote(item, promotion);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void toPromote(ChessItem item, ChessItem promotion){
        if (item != null && item.getRank() == ChessRank.PAWN && item.getPlayer() == promotion.getPlayer()){
            item.setRank(promotion.getRank());
            updateView.setValue(true);
        }
    }

    private void setFinish(ChessColor player) {
        finish.setValue(player == ChessColor.BLACK ? ChessColor.BLACK : ChessColor.WHITE);
        Map<String, Object> values = new HashMap<>();
        values.put(Fields.finished, true);

        database.updateChild(roomPath, values);
    }

    public void promotion(ChessItem item) {
        String path = database.buildPath(new String[]{roomPath, Fields.game, Fields.promotion});
        database.setValue(path, item);
    }
}
