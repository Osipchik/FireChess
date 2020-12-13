package com.example.lab3.ViewModels;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.lab3.Fragments.StatisticFragment;
import com.example.lab3.Models.ModelAuthenticate;
import com.example.lab3.Models.ModelDatabase;
import com.example.lab3.Models.Statistic;
import com.example.lab3.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class StatisticViewModel extends AndroidViewModel {
    private final MutableLiveData<FirebaseRecyclerAdapter> adapter =  new MutableLiveData<>();

    private final ModelDatabase database;
    private final ModelAuthenticate authenticate;


    private final String path;

    public View.OnClickListener onClickListener;

    public StatisticViewModel(@NonNull Application application) {
        super(application);

        authenticate = new ModelAuthenticate();
        database = new ModelDatabase();

        path = "posts/" + authenticate.getUserId();
        fetch();
    }

    public void fetch(){
        Query query = FirebaseDatabase.getInstance().getReference(path);

        FirebaseRecyclerOptions<Statistic> options =
                new FirebaseRecyclerOptions.Builder<Statistic>()
                        .setQuery(query, snapshot -> new Statistic(
                                snapshot.child("id").getValue().toString(),
                                snapshot.child("title").getValue().toString(),
                                snapshot.child("desc").getValue().toString())
                        ).build();


        FirebaseRecyclerAdapter recyclerAdapter = new FirebaseRecyclerAdapter<Statistic, StatisticFragment.ViewHolder>(options) {
            @Override
            public StatisticFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.statistic_item, parent, false);

                return new StatisticFragment.ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(StatisticFragment.ViewHolder holder, final int position, Statistic model) {
                holder.setTxtTitle(model.title);
                holder.setTxtDesc(model.desc);
                holder.root.setOnClickListener(onClickListener);
            }
        };

        adapter.setValue(recyclerAdapter);
        adapter.getValue().startListening();
    }

    public LiveData<FirebaseRecyclerAdapter> getAdapter(){
        return adapter;
    }

//    public void startListening(){
//        adapter.getValue().startListening();
//    }
//
//    public void stopListening(){
//        adapter.getValue().stopListening();
//    }
}
