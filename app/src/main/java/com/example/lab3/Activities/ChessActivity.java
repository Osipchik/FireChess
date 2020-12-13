package com.example.lab3.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.lab3.Dialogs.ConfirmationDialogFragment;
import com.example.lab3.Enums.ChessColor;
import com.example.lab3.Enums.Fields;
import com.example.lab3.Models.ModelDatabase;
import com.example.lab3.R;
import com.example.lab3.ViewModels.ChessViewModel;
import com.example.lab3.ViewModels.GameViewModel;

import java.util.HashMap;
import java.util.Map;

public class ChessActivity extends AppCompatActivity implements ConfirmationDialogFragment.ConfirmationListener {
    private ChessViewModel chessViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess);

        Intent postIntent = getIntent();
        setTitle(postIntent.getStringExtra(Fields.name));

        chessViewModel = ViewModelProviders.of(this).get(ChessViewModel.class);
        chessViewModel.roomId = postIntent.getStringExtra(Fields.Id);
        ChessColor player = ChessColor.valueOf(postIntent.getStringExtra(Fields.player));

        GameViewModel gameViewModel = ViewModelProviders.of(this).get(GameViewModel.class);
        gameViewModel.roomPath = Fields.Rooms + "/" + chessViewModel.roomId;
        gameViewModel.init(player);

        gameViewModel.getMessageId().observe(this, i -> {
            Toast.makeText(this, getString(i), Toast.LENGTH_SHORT).show();
        });

        gameViewModel.getFinish().observe(this, i -> chessViewModel.setStatic());

        findViewById(R.id.button3).setOnClickListener(i -> {
            chessViewModel.setStatic();
        });
    }


    @Override
    public void onBackPressed() {
        ConfirmationDialogFragment dialogFragment = new ConfirmationDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "ConfirmationDialogFragmentTag");
    }

    @Override
    public void confirmButtonClicked() {
        finish();
    }

    @Override
    public void cancelButtonClicked() { }
}