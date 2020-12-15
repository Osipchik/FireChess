package com.example.lab3.Fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab3.R;
import com.example.lab3.ViewModels.StatisticViewModel;

public class StatisticFragment extends Fragment {
    private StatisticViewModel viewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(requireActivity()).get(StatisticViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.statistic_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        viewModel.getAdapter().observe(requireActivity(), recyclerView::setAdapter);

        return view;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView roomName, roomId, status, myScore, rivalScore;
        private final Resources resources;

        public ViewHolder(View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.list_title);
            roomId = itemView.findViewById(R.id.list_Id);
            status = itemView.findViewById(R.id.status);
            myScore = itemView.findViewById(R.id.my_score_view);
            rivalScore = itemView.findViewById(R.id.rival_score_view);
            resources = itemView.getResources();
        }

        public void setRoomName(String name) {
            roomName.setText(name);
        }

        public void setRoomId(String id) {
            roomId.setText(id);
        }

        public void setStatus(boolean isWinner) {
            status.setText(isWinner ? resources.getString(R.string.win) : resources.getString(R.string.defeat));
        }

        public void setMyScore(String score) {
            myScore.setText(score);
        }

        public void setRivalScore(String score) {
            rivalScore.setText(score);
        }
    }
}