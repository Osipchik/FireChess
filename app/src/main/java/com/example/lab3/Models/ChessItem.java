package com.example.lab3.Models;

import com.example.lab3.Enums.ChessColor;
import com.example.lab3.Enums.ChessRank;
import com.example.lab3.R;

import java.util.HashMap;
import java.util.Map;

public class ChessItem {
    private int row, col;
    private ChessColor player;
    private ChessRank rank;
    public int resId;

    public ChessItem() {}

    public ChessItem(int col, int row, ChessColor player, ChessRank rank){
        this.row = row;
        this.col = col;
        this.player = player;
        this.rank = rank;
        resId = getResourceId(player, rank);
    }

    public static int getResourceId(ChessColor itemPlayer, ChessRank itemRank){
        if (itemPlayer == ChessColor.BLACK) {
            switch (itemRank) {
                case KING: return R.drawable.king_black;
                case QUEEN: return R.drawable.queen_black;
                case BISHOP: return R.drawable.bishop_black;
                case ROOK: return R.drawable.rook_black;
                case KNIGHT: return R.drawable.knight_black;
                default: return R.drawable.pawn_black;
            }
        }
        else {
            switch (itemRank) {
                case KING: return R.drawable.king_white;
                case QUEEN: return R.drawable.queen_white;
                case BISHOP: return R.drawable.bishop_white;
                case ROOK: return R.drawable.rook_white;
                case KNIGHT: return R.drawable.knight_white;
                default: return R.drawable.pawn_white;
            }
        }

    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public ChessColor getPlayer() {
        return player;
    }

    public void setPlayer(ChessColor player) {
        this.player = player;
    }

    public ChessRank getRank() {
        return rank;
    }

    public void setRank(ChessRank rank) {
        this.rank = rank;
        if (player != null) {
            resId = getResourceId(player, rank);
        }
    }
}
