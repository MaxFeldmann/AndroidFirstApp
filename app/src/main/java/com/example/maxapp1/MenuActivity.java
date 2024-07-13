package com.example.maxapp1;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.maxapp1.Utillities.SoundPlayer;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;

public class MenuActivity extends AppCompatActivity {

    MaterialButton menu_BTN_start;
    MaterialButton menu_BTN_sensor;
    MaterialButton menu_BTN_score;
    MaterialSwitch control_switch;

    private static final String SLOW = "SLOW";
    private static final String FAST = "FAST";
    private static final String BUTTON = "BUTTON";
    private static final String SENSOR = "SENSOR";
    private String speed = SLOW;

    public SoundPlayer soundPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        onInitPermission();

        findViews();
        initViews();
    }

    private void initViews() {
        menu_BTN_start.setOnClickListener( v -> changeActivityToMain(speed, BUTTON));
        menu_BTN_sensor.setOnClickListener( v -> changeActivityToMain(speed, SENSOR));
        menu_BTN_score.setOnClickListener( v -> changeActivityToScore());
        control_switch.setOnClickListener( v -> changeSpeed());
    }

    private void changeSpeed() {
        if (speed.equals(SLOW))
            speed = FAST;
        else
            speed = SLOW;
    }

    private void changeActivityToMain(String speed, String mode) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.GAME_SPEED, speed);
        intent.putExtra(MainActivity.GAME_MODE, mode);
        startActivity(intent);
        finish();
    }

    private void changeActivityToScore() {
        Intent intent = new Intent(this, ScoreActivity.class);
        startActivity(intent);
        finish();
    }

    private void findViews() {
        menu_BTN_start = findViewById(R.id.menu_BTN_start);
        menu_BTN_sensor = findViewById(R.id.menu_BTN_sensor);
        menu_BTN_score = findViewById(R.id.menu_BTN_score);
        control_switch = findViewById(R.id.control_switch);
    }

    @Override
    protected void onResume() {
        super.onResume();
        soundPlayer = new SoundPlayer(this);
        soundPlayer.playSound(true, R.raw.a_few_jumps_away);
    }

    @Override
    protected void onPause() {
        super.onPause();
        soundPlayer.stopSound();
    }


    public void onInitPermission(){
        if (!(this.checkSelfPermission
                (android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED))
        {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        if (!(this.checkSelfPermission
                (android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED))
        {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
    }
}