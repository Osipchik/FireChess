package com.example.lab3.Models;

import com.example.lab3.Enums.ChessColor;
import com.example.lab3.Enums.ChessRank;
import com.example.lab3.R;

import java.util.HashMap;
import java.util.Map;

public class ChessItem {
    public int row, col;
    public ChessColor player;
    public ChessRank rank;
    public int resId;

    public ChessItem(int col, int row, ChessColor player, ChessRank rank){
        this.row = row;
        this.col = col;
        this.player = player;
        this.rank = rank;
        resId = getResourceId();
    }

    private int getResourceId(){
        if (player == ChessColor.BLACK) {
            switch (rank) {
                case KING: return R.drawable.king_black;
                case QUEEN: return R.drawable.queen_black;
                case BISHOP: return R.drawable.bishop_black;
                case ROOK: return R.drawable.rook_black;
                case KNIGHT: return R.drawable.knight_black;
                default: return R.drawable.pawn_black;
            }
        }
        else {
            switch (rank) {
                case KING: return R.drawable.king_white;
                case QUEEN: return R.drawable.queen_white;
                case BISHOP: return R.drawable.bishop_white;
                case ROOK: return R.drawable.rook_white;
                case KNIGHT: return R.drawable.knight_white;
                default: return R.drawable.pawn_white;
            }
        }

    }

    public Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("rank", rank);
        map.put("player", player);
        map.put("col", col);
        map.put("row", row);
        return map;
    }
}
