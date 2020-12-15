package com.example.lab3.Models;

import com.example.lab3.Enums.ChessColor;
import com.example.lab3.Enums.ChessRank;

import java.util.ArrayList;

public class ModelChessItems {
    private ArrayList<ChessItem> items;

    public ArrayList<ChessItem> getItems() {
        return items;
    }

    public void removeItem(ChessItem item) {
        items.remove(item);
    }

    public void setItems(ArrayList<ChessItem> list){
        items = list;
    }

    public ChessItem getItemAt(int col, int row) {
        if (items != null) {
            for (ChessItem item : items) {
                if (item.getRow() == row && item.getCol() == col){
                    return item;
                }
            }
        }

        return null;
    }

    public ChessItem getItemAt(int col, int row, ChessColor player) {
        if (items != null) {
            for (ChessItem item : items) {
                if (item.getRow() == row && item.getCol() == col && item.getPlayer() == player){
                    return item;
                }
            }
        }

        return null;
    }

    public ArrayList<ChessItem> createDefault(){
        ArrayList<ChessItem> piecesBox = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            piecesBox.add(new ChessItem(i * 7, 0, ChessColor.WHITE, ChessRank.ROOK));
            piecesBox.add(new ChessItem(i * 7, 7, ChessColor.BLACK, ChessRank.ROOK));

            piecesBox.add(new ChessItem(1 + i * 5, 0, ChessColor.WHITE, ChessRank.KNIGHT));
            piecesBox.add(new ChessItem(1 + i * 5, 7, ChessColor.BLACK, ChessRank.KNIGHT));

            piecesBox.add(new ChessItem(2 + i * 3, 0, ChessColor.WHITE, ChessRank.BISHOP));
            piecesBox.add(new ChessItem(2 + i * 3, 7, ChessColor.BLACK, ChessRank.BISHOP));
        }

        for (int i = 0; i < 8; i++) {
            piecesBox.add(new ChessItem(i, 1, ChessColor.WHITE, ChessRank.PAWN));
            piecesBox.add(new ChessItem(i, 6, ChessColor.BLACK, ChessRank.PAWN));
        }

        piecesBox.add(new ChessItem(3, 0, ChessColor.WHITE, ChessRank.QUEEN));
        piecesBox.add(new ChessItem(3, 7, ChessColor.BLACK, ChessRank.QUEEN));
        piecesBox.add(new ChessItem(4, 0, ChessColor.WHITE, ChessRank.KING));
        piecesBox.add(new ChessItem(4, 7, ChessColor.BLACK, ChessRank.KING));

        return piecesBox;
    }
}
