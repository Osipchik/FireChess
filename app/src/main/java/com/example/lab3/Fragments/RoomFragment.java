package com.example.lab3.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lab3.Activities.ChessActivity;
import com.example.lab3.Enums.ChessColor;
import com.example.lab3.Enums.Fields;
import com.example.lab3.Models.RoomModel;
import com.example.lab3.R;
import com.example.lab3.ViewModels.RoomViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class RoomFragment extends Fragment {
    private RoomViewModel viewModel;
    private ProgressBar progressBar;

    private Button startButton;
    private TextInputEditText inputEditText;
    private TextInputLayout inputLayout;

    private TextView roomIdView;
    private LinearLayout roomIdLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(requireActivity()).get(RoomViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);
        startButton = view.findViewById(R.id.start_game_button);
        Button createButton = view.findViewById(R.id.create_button);
        Button connectButton = view.findViewById(R.id.connect_button);
        inputEditText = view.findViewById(R.id.room_Id);
        roomIdView = view.findViewById(R.id.room_Id_view);
        inputLayout = view.findViewById(R.id.room_Id_layout);
        roomIdLayout = view.findViewById(R.id.room_Id_view_layout);
        progressBar = view.findViewById(R.id.room_progress_bar);

        inputEditText.addTextChangedListener(roomIdTextWatcher);

        startButton.setOnClickListener(this::onStartClick);
        createButton.setOnClickListener(this::onCreateClick);
        connectButton.setOnClickListener(this::onConnectClick);

        viewModel.isConnected().observe(requireActivity(), this::onConnected);

        return view;
    }

    private final TextWatcher roomIdTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 0) {
                inputLayout.setErrorEnabled(true);
                inputLayout.setError(getString(R.string.room_Id_empty));
            }
            else {
                inputLayout.setErrorEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };

    private void onStartClick(View i) {
        RoomModel model = viewModel.isConnected().getValue();

        Intent intent = new Intent(requireActivity(), ChessActivity.class);
        intent.putExtra(Fields.name, model.getRoomName());
        intent.putExtra(Fields.Id, model.getRoomId());
        intent.putExtra(Fields.Users, viewModel.getMyId());
        intent.putExtra(Fields.player, model.getPlayer().toString());
        startActivity(intent);
    }

    private void onCreateClick(View i) {
        String name = inputEditText.getText().toString();
        if (name.trim().length() == 0) {
            inputLayout.setErrorEnabled(true);
            inputLayout.setError(getString(R.string.room_Id_empty));
        }
        else {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            viewModel.createRoom(inputEditText.getText().toString());
        }
    }

    private void onConnectClick(View i) {
        String name = inputEditText.getText().toString();
        if (name.trim().length() == 0) {
            inputLayout.setErrorEnabled(true);
            inputLayout.setError(getString(R.string.room_Id_empty));
        }
        else {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            viewModel.connectToRoom(inputEditText.getText().toString());
        }
    }

    private void onConnected(RoomModel model) {
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        if (model != null) {
            Toast.makeText(requireActivity(), getString(R.string.connected), Toast.LENGTH_SHORT).show();

            startButton.setEnabled(true);
            inputLayout.setErrorEnabled(false);

            roomIdLayout.setVisibility(View.VISIBLE);
            roomIdView.setText(model.getRoomId());
        }
        else {
            startButton.setEnabled(false);
            inputLayout.setErrorEnabled(true);

            roomIdLayout.setVisibility(View.INVISIBLE);
            inputLayout.setError(getString(R.string.room_doees_not_exist));

        }
    }
}