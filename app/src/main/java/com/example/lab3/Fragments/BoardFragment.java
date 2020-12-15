package com.example.lab3.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lab3.CustomView.ChessBoard;
import com.example.lab3.Dialogs.PawnPromotionDialog;
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
        board.setChessDelegate(this);
        board.setPlayer(viewModel.getPlayer());

        viewModel.updateView().observe(requireActivity(), i -> {
            board.invalidate();
            viewModel.saveGame();
        });

        return view;
    }

    @Override
    public ChessItem itemAt(int col, int row) {
        return viewModel.getItemAt(col, row);
    }

    @Override
    public void moveTo(ChessItem selectedItem, int currentCol, int currentRow) {
        if (!viewModel.getIsFinished()){
            switch (selectedItem.getRank()){
                case PAWN: movePawn(selectedItem, currentCol, currentRow); break;
                case BISHOP: moveBishop(selectedItem, currentCol, currentRow); break;
                case ROOK: moveRook(selectedItem, currentCol, currentRow); break;
                case QUEEN: moveQueen(selectedItem, currentCol, currentRow); break;
                case KING: moveKing(selectedItem, currentCol, currentRow); break;
                case KNIGHT: moveKnight(selectedItem, currentCol, currentRow); break;
            }
        }
    }

    private void movePawn(ChessItem selectedItem, int currentCol, int currentRow){
        int rowDiff = currentRow - selectedItem.getRow();
        int colDiff = Math.abs(currentCol - selectedItem.getCol());

        if ((viewModel.getPlayer() == ChessColor.WHITE && (rowDiff == 1 || rowDiff == 2) ||
                viewModel.getPlayer() == ChessColor.BLACK && (rowDiff == -1 || rowDiff == -2)) && colDiff <= 1) {

            ChessItem piece = itemAt(currentCol, currentRow);
            if (piece != null && piece.getPlayer() != viewModel.getPlayer()){
                if (currentCol != selectedItem.getCol() && Math.abs(rowDiff) == 1) {
                    viewModel.removeItem(piece);
                    viewModel.moveItem(selectedItem, currentCol, currentRow, false);
                    showPromotionDialog(selectedItem);
                }
            }
            else if (currentCol == selectedItem.getCol()) {
                viewModel.moveItem(selectedItem, selectedItem.getCol(), currentRow, false);
                showPromotionDialog(selectedItem);
            }
        }
    }

    private void removePiece(int currentCol, int currentRow){
        ChessItem piece = itemAt(currentCol, currentRow);
        if (piece != null && piece.getPlayer() != viewModel.getPlayer()) {
             viewModel.removeItem(piece);
        }
    }

    private boolean moveBishop(ChessItem selectedItem, int currentCol, int currentRow) {
        int rowDiff = Math.abs(currentRow - selectedItem.getRow());
        int colDiff = Math.abs(currentCol - selectedItem.getCol());

        if (rowDiff == colDiff) {
            int colFactor = selectedItem.getCol() - currentCol > 0 ? -1 : 1;
            int rowFactor = selectedItem.getRow() - currentRow > 0 ? -1 : 1;

            if (checkPath(colFactor, rowFactor, currentCol, currentRow, selectedItem.getCol(), selectedItem.getRow())) {
                removePiece(currentCol, currentRow);
                viewModel.moveItem(selectedItem, currentCol, currentRow, true);

                return true;
            }
        }

        return false;
    }

    private void moveRook(ChessItem selectedItem, int currentCol, int currentRow) {
        if (currentCol == selectedItem.getCol() || currentRow == selectedItem.getRow()) {
            int colFactor = Integer.compare(currentCol, selectedItem.getCol());
            int rowFactor = Integer.compare(currentRow, selectedItem.getRow());

            if (checkPath(colFactor, rowFactor, currentCol, currentRow, selectedItem.getCol(), selectedItem.getRow())){
                removePiece(currentCol, currentRow);

                viewModel.moveItem(selectedItem, currentCol, currentRow, true);
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
        int rowDiff = Math.abs(currentRow - selectedItem.getRow());
        int colDiff = Math.abs(currentCol - selectedItem.getCol());

        if (rowDiff <= 1 && colDiff <= 1) {
            removePiece(currentCol, currentRow);
            viewModel.moveItem(selectedItem, currentCol, currentRow, true);
        }
    }

    private void moveKnight(ChessItem selectedItem, int currentCol, int currentRow) {
        int rowDiff = Math.abs(currentRow - selectedItem.getRow());
        int colDiff = Math.abs(currentCol - selectedItem.getCol());

        if ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)) {
            removePiece(currentCol, currentRow);
            viewModel.moveItem(selectedItem, currentCol, currentRow, true);
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

    private void showPromotionDialog(ChessItem item) {
        if (item.getPlayer() == ChessColor.WHITE && item.getRow() == 7 || item.getPlayer() == ChessColor.BLACK && item.getRow() == 0){
            PawnPromotionDialog dialog = new PawnPromotionDialog(item);
            dialog.setCancelable(false);
            dialog.show(getFragmentManager(), "ChangePawnDialog");
        }
        else {
            viewModel.updateRound();
        }
    }
}