package com.example.lab3.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.lab3.Enums.ChessColor;
import com.example.lab3.Enums.ChessRank;
import com.example.lab3.Models.ChessItem;
import com.example.lab3.Models.ModelAuthenticate;
import com.example.lab3.Models.ModelDatabase;
import com.example.lab3.Models.ModelChessItems;
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
    private final ModelChessItems items;

    private ChessColor player;
    private Boolean isLocked;
    private String roomPath;
    private boolean isFinished;
    private int playerFactor;

    public GameViewModel(@NonNull Application application) {
        super(application);

        items = new ModelChessItems();

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
            ModelAuthenticate authenticate = new ModelAuthenticate();

            database.setValue(roomPath + "/game/remove", item);
            database.setValue(roomPath +"/Users/" + authenticate.getUserId() + "/score/" + new Date(), item);
        }
    }

    public void moveItem(ChessItem item, int col, int row, boolean updateRound) {
        if (!isLocked) {
            if (updateRound) {
                updateRound();
            }

            Map<String, Object> map = new HashMap<>();
            map.put("fromCol", item.getCol());
            map.put("fromRow", item.getRow());
            map.put("toCol", col);
            map.put("toRow", row);
            database.setValue(roomPath +"/game/move", map);

            item.setRow(row);
            item.setCol(col);;
        }
        else {
            messageId.setValue(R.string.waiting_round);
        }
    }

    public void updateRound(){
        isLocked = true;
        database.setValue(roomPath + "/game/round", player);
    }

    public LiveData<ChessColor> getFinish() {
        return finish;
    }

    public LiveData<Integer> getMessageId() {
        return messageId;
    }

    public void saveGame() {
        if (player == ChessColor.WHITE) {
            database.setValue(roomPath + "/game/state", items.getItems());
        }
    }

    public void init(ChessColor player, String roomId) {
        this.player = player;
        playerFactor = player == ChessColor.BLACK ? 7 : 0;
        this.roomPath = "Rooms/" + roomId;

        loadLasGame();
        loadRound();
    }

    private void loadLasGame() {
        database.getReference(roomPath).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isFinished = snapshot.child("finished").getValue(boolean.class);
                loadField(snapshot.child("game").child("state"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void loadField(DataSnapshot snapshot){
        ArrayList<ChessItem> piecesBox = new ArrayList<>();

        if (snapshot.exists()) {
            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                ChessItem item = dataSnapshot.getValue(ChessItem.class);
                piecesBox.add(item);
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
        database.getReference(roomPath + "/game/round").addValueEventListener(new ValueEventListener() {
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
        database.getReference(roomPath + "/game/move").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int fromCol = snapshot.child("fromCol").getValue(int.class);
                    int fromRow = snapshot.child("fromRow").getValue(int.class);

                    ChessItem item = items.getItemAt(fromCol, fromRow);
                    if (item != null) {
                        item.setRow(snapshot.child("toRow").getValue(int.class));
                        item.setCol(snapshot.child("toCol").getValue(int.class));
                        updateView.setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void removeItemListening() {
        database.getReference(roomPath + "/game/remove").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int fromCol = snapshot.child("col").getValue(int.class);
                    int fromRow = snapshot.child("row").getValue(int.class);

                    ChessColor color = snapshot.child("player").getValue(ChessColor.class);

                    ChessItem item = items.getItemAt(fromCol, fromRow, color);
                    if (item != null) {
                        items.removeItem(item);
                        updateView.setValue(true);

                        if (item.getRank() == ChessRank.KING) {
                            isLocked = true;
                            isFinished = true;
                            setFinish(item.getPlayer());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void promotionListening() {
        database.getReference(roomPath + "/game/promotion").addValueEventListener(new ValueEventListener() {
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
        values.put("finished", true);

        database.updateChild(roomPath, values);
    }

    public void promotion(ChessItem item) {
        database.setValue(roomPath +"/game/promotion", item);
    }
}
