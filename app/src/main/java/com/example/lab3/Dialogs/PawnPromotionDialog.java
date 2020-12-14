package com.example.lab3.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.lab3.Enums.ChessRank;
import com.example.lab3.Models.ChessItem;
import com.example.lab3.R;

public class PawnPromotionDialog extends DialogFragment {
    public interface ChangePawnDialogListener {
        void confirmSelectedClicked(ChessItem item);
    }

    private PawnPromotionDialog.ChangePawnDialogListener listener;
    private int checkedItem = 0;
    private final ChessItem item;

    public PawnPromotionDialog(ChessItem item) {
        ChessItem prom = new ChessItem();
        prom.setCol(item.getCol());
        prom.setRow(item.getRow());
        prom.setPlayer(item.getPlayer());
        this.item = prom;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (PawnPromotionDialog.ChangePawnDialogListener) getActivity();
        }
        catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement ChangePawnDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] ranks = {ChessRank.QUEEN.toString(),
                ChessRank.BISHOP.toString(),
                ChessRank.KNIGHT.toString(),
                ChessRank.ROOK.toString()};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setCancelable(false);
        builder.setTitle(R.string.replace_pawn);
        builder.setSingleChoiceItems(ranks, checkedItem, (dialog, which) -> checkedItem = which);
        builder.setPositiveButton(R.string.select, (dialog, id) -> {
            item.setRank(ChessRank.valueOf(ranks[checkedItem]));
            listener.confirmSelectedClicked(item);
        });

        return builder.create();
    }
}
