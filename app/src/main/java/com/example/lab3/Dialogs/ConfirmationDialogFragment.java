package com.example.lab3.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.lab3.R;

public class ConfirmationDialogFragment extends DialogFragment {

    public interface ConfirmationListener {
        void confirmButtonClicked();
        void cancelButtonClicked();
    }

    private ConfirmationListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ConfirmationListener) getActivity();
        }
        catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement ConfirmationListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(R.string.leave_game_dialog)
                .setPositiveButton(R.string.leave, (dialog, id) -> listener.confirmButtonClicked())
                .setNegativeButton(R.string.cancel, (dialog, id) -> listener.cancelButtonClicked());

        return builder.create();
    }
}
