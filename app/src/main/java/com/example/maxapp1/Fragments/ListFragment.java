package com.example.maxapp1.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maxapp1.Interfaces.Callback_ListItemClicked;
import com.example.maxapp1.Models.HighScoreList;
import com.example.maxapp1.R;
import com.example.maxapp1.Utillities.HighScoreAdapter;
import com.example.maxapp1.Utillities.SharedPreferencesManager;
import com.google.gson.Gson;

public class ListFragment extends Fragment {

    private RecyclerView score_LST_highscores;

    Callback_ListItemClicked callbackListItemClicked;

    public ListFragment() {
        // Required empty public constructor
    }

    public void setCallbackListItemClicked(Callback_ListItemClicked callbackListItemClicked) {
        this.callbackListItemClicked = callbackListItemClicked;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        findViews(v);
        initViews();
        return v;
    }

    private void initViews() {

        HighScoreAdapter highScoreAdapter = new HighScoreAdapter(loadHighScoreList());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        score_LST_highscores.setLayoutManager(linearLayoutManager);
        score_LST_highscores.setAdapter(highScoreAdapter);
        highScoreAdapter.setHighScoreCallback((highScore, position) -> itemClicked(highScore.getLat(), highScore.getLon()));
    }

    public HighScoreList loadHighScoreList()
    {
        Gson gson = new Gson();
        String highScoreListAsJson = SharedPreferencesManager
                .getInstance()
                .getString("highScoreList", "");
        HighScoreList highScoreList = gson.fromJson(highScoreListAsJson, HighScoreList.class);
        if (highScoreList == null)
            highScoreList = new HighScoreList();
        return highScoreList;
    }

    private void itemClicked(double lat, double lon) {
        if (callbackListItemClicked != null)
            callbackListItemClicked.listItemClicked(lat, lon);
    }

    private void findViews(View v) {
        score_LST_highscores = v.findViewById(R.id.score_LST_highscores);
    }
}