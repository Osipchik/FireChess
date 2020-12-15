package com.example.lab3.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab3.Dialogs.ConfirmationDialogFragment;
import com.example.lab3.Dialogs.PawnPromotionDialog;
import com.example.lab3.Enums.ChessColor;
import com.example.lab3.Enums.ChessRank;
import com.example.lab3.Models.ChessItem;
import com.example.lab3.R;
import com.example.lab3.ViewModels.ChessViewModel;
import com.example.lab3.ViewModels.GameViewModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

public class ChessActivity extends AppCompatActivity
        implements ConfirmationDialogFragment.ConfirmationListener,
        PawnPromotionDialog.ChangePawnDialogListener {
    private ChessViewModel chessViewModel;
    private GameViewModel gameViewModel;
    private RecyclerView rivalRecyclerView, myRecyclerView;
    ChessColor player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess);

        Intent postIntent = getIntent();
        String roomName = postIntent.getStringExtra("roomName");
        String myId = postIntent.getStringExtra("Users");
        String roomId = postIntent.getStringExtra("roomId");
        player = ChessColor.valueOf(postIntent.getStringExtra("player"));

        setTitle(roomName);

        chessViewModel = ViewModelProviders.of(this).get(ChessViewModel.class);
        chessViewModel.setRoomId(roomId);
        chessViewModel.myId = myId;
        chessViewModel.roomName = roomName;

        gameViewModel = ViewModelProviders.of(this).get(GameViewModel.class);
//        String roomPath = Fields.Rooms + "/" + chessViewModel.roomId;
        gameViewModel.init(player, roomId);

        gameViewModel.getMessageId().observe(this, i -> {
            Toast.makeText(this, getString(i), Toast.LENGTH_SHORT).show();
        });

        gameViewModel.getFinish().observe(this, this::onFinish);

        setRecyclerViews();
    }

    private void onFinish(ChessColor i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(i != player ? getString(R.string.win) : getString(R.string.defeat))
                .setMessage(R.string.party_over)
                .setPositiveButton(R.string.ok, null).show();

        int myScore = myRecyclerView.getAdapter().getItemCount();
        int scoreRival = rivalRecyclerView.getAdapter().getItemCount();
        chessViewModel.setStatic(myScore, scoreRival, i != player);
    }

    private void setRecyclerViews() {
        chessViewModel.addUsersListener();
        chessViewModel.rivalId().observe(this, i -> {
            rivalRecyclerView = findViewById(R.id.rival_score);
            rivalRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rivalRecyclerView.setAdapter(getAdapter(i));
        });

        myRecyclerView = findViewById(R.id.my_score);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        myRecyclerView.setAdapter(getAdapter(chessViewModel.myId));
    }

    private FirebaseRecyclerAdapter<?, ?> getAdapter(String userId) {
        Query query = chessViewModel.getScoreReference(userId);

        FirebaseRecyclerOptions<Integer> options =
                new FirebaseRecyclerOptions.Builder<Integer>()
                        .setQuery(query.orderByKey(), snapshot ->  ChessItem.getResourceId(
                                    snapshot.child("player").getValue(ChessColor.class),
                                    snapshot.child("rank").getValue(ChessRank.class)))
                        .build();


        FirebaseRecyclerAdapter<?, ?> recyclerAdapter = new FirebaseRecyclerAdapter<Integer, ChessActivity.ViewHolder>(options) {

            @Override
            public ChessActivity.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_item, parent, false);

                return new ChessActivity.ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(ChessActivity.ViewHolder holder, final int position, Integer resourceId) {
                holder.setImage(resourceId);
            }
        };

        recyclerAdapter.startListening();

        return recyclerAdapter;
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


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
        }

        public void setImage(int resourceId) {
            image.setImageResource(resourceId);
        }
    }

    @Override
    public void confirmSelectedClicked(ChessItem item) {
        gameViewModel.promotion(item);
        gameViewModel.updateRound();
    }
}