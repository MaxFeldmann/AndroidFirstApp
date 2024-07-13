package com.example.maxapp1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.maxapp1.Fragments.ListFragment;
import com.example.maxapp1.Fragments.MapFragment;
import com.example.maxapp1.Interfaces.Callback_ListItemClicked;
import com.google.android.material.button.MaterialButton;

public class ScoreActivity extends AppCompatActivity{

    private FrameLayout score_FRAME_list;
    private FrameLayout score_FRAME_map;

    private ListFragment listFragment;
    private MapFragment mapFragment;
    private MaterialButton score_BTN_to_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        findViews();
        initViews();

    }

    private void initViews() {
        score_BTN_to_menu.setOnClickListener(v -> changeActivityToMenu());
        listFragment = new ListFragment();
        listFragment.setCallbackListItemClicked(new Callback_ListItemClicked() {
            @Override
            public void listItemClicked(double lat, double lon) {
                mapFragment.zoom(lat, lon);
            }
        });
        getSupportFragmentManager().beginTransaction().add(R.id.score_FRAME_list,listFragment).commit();
        mapFragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.score_FRAME_map,mapFragment).commit();
    }

    private void changeActivityToMenu() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    private void findViews() {
        score_FRAME_list = findViewById(R.id.score_FRAME_list);
        score_FRAME_map = findViewById(R.id.score_FRAME_map);
        score_BTN_to_menu = findViewById(R.id.score_BTN_to_menu);
    }
}