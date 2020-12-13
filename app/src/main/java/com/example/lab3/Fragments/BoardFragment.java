package com.example.lab3.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lab3.CustomView.ChessBoard;
import com.example.lab3.Enums.ChessColor;
import com.example.lab3.Models.ChessItem;
import com.example.lab3.R;
import com.example.lab3.ViewModels.GameViewModel;

public class BoardFragment extends Fragment implements ChessBoard.IChessDelegate {
    private GameViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(requireActivity()).get(GameViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board, container, false);

        ChessBoard board = view.findViewById(R.id.chess_view);
        board.chessDelegate = this;
        board.player = viewModel.getPlayer();

        viewModel.updateView().observe(requireActivity(), i -> {
            board.invalidate();
            viewModel.saveGame();
        });

        return view;
    }

    @Override
    public ChessItem pieceAt(int col, int row) {
        return viewModel.getItemAt(col, row);
    }

    @Override
    public void moveTo(ChessItem selectedItem, int currentCol, int currentRow) {
        switch (selectedItem.rank){
            case PAWN: movePawn(selectedItem, currentCol, currentRow); break;
            case BISHOP: moveBishop(selectedItem, currentCol, currentRow); break;
            case ROOK: moveRook(selectedItem, currentCol, currentRow); break;
            case QUEEN: moveQueen(selectedItem, currentCol, currentRow); break;
            case KING: moveKing(selectedItem, currentCol, currentRow); break;
            case KNIGHT: moveKnight(selectedItem, currentCol, currentRow); break;
        }
    }

    private void movePawn(ChessItem selectedItem, int currentCol, int currentRow){
        int rowDiff = currentRow - selectedItem.row;
        int colDiff = Math.abs(currentCol - selectedItem.col);

        if ((viewModel.getPlayer() == ChessColor.WHITE && (rowDiff == 1 || rowDiff == 2) ||
                viewModel.getPlayer() == ChessColor.BLACK && (rowDiff == -1 || rowDiff == -2)) && colDiff <= 1) {

            ChessItem piece = pieceAt(currentCol, currentRow);
            if (piece != null && piece.player != viewModel.getPlayer()){
                if (currentCol != selectedItem.col && Math.abs(rowDiff) == 1) {
                    viewModel.removeItem(piece);
                    viewModel.moveItem(selectedItem, currentCol, currentRow);
                }
            }
            else {
                if (currentCol == selectedItem.col) {
                    viewModel.moveItem(selectedItem, selectedItem.col, currentRow);
                }
            }
        }
    }

    private void removePiece(int currentCol, int currentRow){
        ChessItem piece = pieceAt(currentCol, currentRow);
        if (piece != null && piece.player != viewModel.getPlayer()) {
             viewModel.removeItem(piece);
        }
    }

    private boolean moveBishop(ChessItem selectedItem, int currentCol, int currentRow) {
        int rowDiff = Math.abs(currentRow - selectedItem.row);
        int colDiff = Math.abs(currentCol - selectedItem.col);

        if (rowDiff == colDiff) {
            int colFactor = selectedItem.col - currentCol > 0 ? -1 : 1;
            int rowFactor = selectedItem.row - currentRow > 0 ? -1 : 1;

            if (checkPath(colFactor, rowFactor, currentCol, currentRow, selectedItem.col, selectedItem.row)) {
                removePiece(currentCol, currentRow);
                viewModel.moveItem(selectedItem, currentCol, currentRow);

                return true;
            }
        }

        return false;
    }

    private void moveRook(ChessItem selectedItem, int currentCol, int currentRow) {
        if (currentCol == selectedItem.col || currentRow == selectedItem.row) {
            int colFactor = Integer.compare(currentCol, selectedItem.col);
            int rowFactor = Integer.compare(currentRow, selectedItem.row);

            if (checkPath(colFactor, rowFactor, currentCol, currentRow, selectedItem.col, selectedItem.row)){
                removePiece(currentCol, currentRow);

                viewModel.moveItem(selectedItem, currentCol, currentRow);
            }
        }
    }

    private void moveQueen(ChessItem selectedItem, int currentCol, int currentRow) {
        boolean isMoved = moveBishop(selectedItem, currentCol, currentRow);
        if (!isMoved){
            moveRook(selectedItem, currentCol, currentRow);
        }
    }

    private void moveKing(ChessItem selectedItem, int currentCol, int currentRow) {
        int rowDiff = Math.abs(currentRow - selectedItem.row);
        int colDiff = Math.abs(currentCol - selectedItem.col);

        if (rowDiff <= 1 && colDiff <= 1) {
            removePiece(currentCol, currentRow);
            viewModel.moveItem(selectedItem, currentCol, currentRow);
        }
    }

    private void moveKnight(ChessItem selectedItem, int currentCol, int currentRow) {
        int rowDiff = Math.abs(currentRow - selectedItem.row);
        int colDiff = Math.abs(currentCol - selectedItem.col);

        if ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)) {
            removePiece(currentCol, currentRow);
            viewModel.moveItem(selectedItem, currentCol, currentRow);
        }
    }

    private boolean checkPath(int colFactor, int rowFactor, int currentCol, int currentRow, int fromCol, int fromRow){
        int col = currentCol - colFactor;
        int row = currentRow - rowFactor;

        while (col != fromCol || row != fromRow){
            ChessItem item = viewModel.getItemAt(col, row);
            if (item != null) {
                return false;
            }

            col -= colFactor;
            row -= rowFactor;
        }

        return true;
    }
}