package com.example.lab3.ViewModels;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.lab3.Enums.Fields;
import com.example.lab3.Fragments.StatisticFragment;
import com.example.lab3.Models.ModelAuthenticate;
import com.example.lab3.Models.ModelDatabase;
import com.example.lab3.Models.Statistic;
import com.example.lab3.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

public class StatisticViewModel extends AndroidViewModel {
    private final MutableLiveData<FirebaseRecyclerAdapter<?, ?>> adapter =  new MutableLiveData<>();
    private final ModelDatabase database;
    private final String path;

    public LiveData<FirebaseRecyclerAdapter<?, ?>> getAdapter() {
        return adapter;
    }

    public StatisticViewModel(@NonNull Application application) {
        super(application);

        ModelAuthenticate authenticate = new ModelAuthenticate();
        database = new ModelDatabase();

        path = Fields.Statistic + "/" + authenticate.getUserId();
        fetch();
    }

    public void fetch(){
        Query query = database.getReference(path);

        FirebaseRecyclerOptions<Statistic> options =
                new FirebaseRecyclerOptions.Builder<Statistic>()
                        .setQuery(query, Statistic.class).build();

        FirebaseRecyclerAdapter<?, ?> recyclerAdapter = new FirebaseRecyclerAdapter<Statistic, StatisticFragment.ViewHolder>(options) {
            @Override
            public StatisticFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.statistic_item, parent, false);

                return new StatisticFragment.ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(StatisticFragment.ViewHolder holder, final int position, Statistic model) {
                holder.setRoomName(model.getRoomName());
                holder.setRoomId(model.getRoomId());
                holder.setStatus(model.isWinner() ? "win" : "defeat");
                holder.setMyScore(model.getMyScore());
                holder.setRivalScore(model.getRivalScore());
            }
        };

        recyclerAdapter.startListening();
        adapter.setValue(recyclerAdapter);
    }
}
