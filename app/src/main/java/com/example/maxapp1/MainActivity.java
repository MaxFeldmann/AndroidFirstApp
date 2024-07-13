package com.example.maxapp1;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;

import com.example.maxapp1.Interfaces.MoveCallback;
import com.example.maxapp1.Logic.GameManager;
import com.example.maxapp1.Models.HighScore;
import com.example.maxapp1.Models.HighScoreList;
import com.example.maxapp1.Utillities.MoveDetector;
import com.example.maxapp1.Utillities.SharedPreferencesManager;
import com.example.maxapp1.Utillities.SoundPlayer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private final int FINE_PERMISSION_CODE = 1;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    public static String GAME_SPEED = "GAME_SPEED";
    public static String GAME_MODE = "GAME_MODE";

    private static final long SHORT_DELAY = 600L;
    private static final long LONG_DELAY = 1200L;
    private long delay;
    private FloatingActionButton main_BTN_left;
    private FloatingActionButton main_BTN_right;

    private GameManager gameManager;
    private AppCompatImageView[] main_IMG_hearts;
    private AppCompatImageView[] main_IMG_aliens;
    private AppCompatImageView[] main_IMG_ships;
    private AppCompatImageView[] main_IMG_metals;
    MaterialTextView main_LBL_score;

    private Timer timer;
    private boolean timerOn = false;
    private int lastRound = 0;

    private MoveDetector moveDetector;
    private SoundPlayer soundPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        findViews();
        gameManager = new GameManager(main_IMG_hearts.length);
        initViews();
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null)
                    currentLocation = location;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getLastLocation();
            else {
                Toast.makeText(this, "Scores will not be saved until location is enabled :)", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void refreshUI() {
        //Lost:
        if (gameManager.isGameLost()){
            stopTimer();
            updateHighScoreList();
            changeActivityToScore();
        }
        //GAME ON!
        else{
            moveNext();
            main_LBL_score.setText(String.valueOf(gameManager.getScore()));
        }
    }

    private void changeActivityToScore() {
        Intent intent = new Intent(this, ScoreActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateHighScoreList() {
        HighScoreList highScoreList;
        getLastLocation();
        Gson gson = new Gson();
        String highScoreListAsJson = SharedPreferencesManager
                .getInstance()
                .getString("highScoreList", "");
        if (highScoreListAsJson == null || highScoreListAsJson.isEmpty())
            highScoreList = new HighScoreList();
        else
            highScoreList = gson.fromJson(highScoreListAsJson, HighScoreList.class);
        //Add high score only if current location is available
        if (currentLocation != null)
            highScoreList.addHighScore(new HighScore(currentLocation.getLatitude(),
                            currentLocation.getLongitude(), gameManager.getScore()));
        else
            highScoreList.addHighScore(new HighScore(0.0,
                    0.0, gameManager.getScore()));
        SharedPreferencesManager.getInstance()
                .putString("highScoreList", gson.toJson(highScoreList));
    }

    private void initViews() {
        Intent previousIntent = getIntent();
        GAME_SPEED = previousIntent.getStringExtra(GAME_SPEED);
        GAME_MODE = previousIntent.getStringExtra(GAME_MODE);
        if (GAME_MODE.equals("BUTTON"))
        {
            main_BTN_left.setOnClickListener( v -> answerClicked(0));
            main_BTN_right.setOnClickListener( v -> answerClicked(1));
        }
        else
        {
            main_BTN_left.setVisibility(View.INVISIBLE);
            main_BTN_right.setVisibility(View.INVISIBLE);
            initMoveDetector();
        }
        setVisibility();
        delay = GAME_SPEED.equals("SLOW") ? LONG_DELAY : SHORT_DELAY;
    }

    private void initMoveDetector() {
        moveDetector = new MoveDetector(this,
                new MoveCallback() {
                    @Override
                    public void movePosX() {
                        answerClicked(0);
                    }

                    @Override
                    public void moveNegX() {
                        answerClicked(1);
                    }

                    @Override
                    public void moveNegY() {
                        changeSpeed(true, gameManager.getRound());
                    }

                    @Override
                    public void movePosY() {
                        changeSpeed(false, gameManager.getRound());
                    }
                });
    }

    private void changeSpeed(boolean speedUp, int round) {
        if (round != lastRound)
        {
            lastRound = round;
            if (speedUp && delay != 300) {
                delay -= 100;
                stopTimer();
                startTimer();
            } else if (!speedUp && delay != 1500) {
                delay += 100;
                stopTimer();
                startTimer();
            }
        }
    }

    private void setVisibility()
    {
        for (AppCompatImageView mainImgAlien : main_IMG_aliens)
            mainImgAlien.setVisibility(View.INVISIBLE);
        for (AppCompatImageView mainImgMetal : main_IMG_metals)
            mainImgMetal.setVisibility(View.INVISIBLE);
        for (AppCompatImageView mainImgShip : main_IMG_ships)
            mainImgShip.setVisibility(View.INVISIBLE);
        main_IMG_ships[2].setVisibility(View.VISIBLE);
        for (AppCompatImageView mainImgHeart : main_IMG_hearts)
            mainImgHeart.setVisibility(View.VISIBLE);
    }

    private void startTimer() {
        if (!timerOn) {
            timerOn = true;
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> refreshUI());
                }
            }, delay, delay);
        }
    }

    private void stopTimer() {
        if (timerOn) {
            timerOn = false;
            timer.cancel();
        }
    }

    private void answerClicked(int ans) {
        boolean didMove = gameManager.moveShip(ans);
        int index = gameManager.getShipIndex();
        int hitOrLoot = checkHit();
        if (didMove)
        {
            if (hitOrLoot == 1)
                main_IMG_aliens[(gameManager.getCols() * (gameManager.getRows() - 1)) + index].setVisibility(View.INVISIBLE);
            else if (hitOrLoot == 2)
                main_IMG_metals[(gameManager.getCols() * (gameManager.getRows() - 1)) + index].setVisibility(View.INVISIBLE);
            if (ans == 0)
            {
                main_IMG_ships[index + 1].setVisibility(View.INVISIBLE);
                main_IMG_ships[index].setVisibility(View.VISIBLE);
            }
            else
            {
                main_IMG_ships[index - 1].setVisibility(View.INVISIBLE);
                main_IMG_ships[index].setVisibility(View.VISIBLE);
            }
        }
    }

    private int checkHit() {
        int hitOrLoot = gameManager.isHit();
        //Hit with alien
        if (hitOrLoot == 1) {
            main_IMG_hearts[main_IMG_hearts.length - gameManager.getDmg()].setVisibility(View.INVISIBLE);
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VibrationEffect.createOneShot(250,25));
            String msg;
            if (gameManager.isGameLost())
                msg = "Game Over!";
            else
                msg = "Crash!";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            SoundPlayer soundPlayer = new SoundPlayer(this);
            soundPlayer.playSound(false, R.raw.crash_7075);
        }
        //Found metal
        else if (hitOrLoot == 2){
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VibrationEffect.createOneShot(250,25));
            SoundPlayer soundPlayer = new SoundPlayer(this);
            soundPlayer.playSound(false, R.raw.metal_power_up);
        }
        return hitOrLoot;
    }

    private void moveNext()
    {
        gameManager.moveNext();
        int[][] newMat = gameManager.getLogicMat();
        int rowLen = gameManager.getRows();
        int colLen = gameManager.getCols();
        int shipIndex = gameManager.getShipIndex();
        for (int i = 0; i < rowLen - 1; i++)
        {
            for (int j = 0; j < colLen; j++)
            {
                if (newMat[i][j] == 1)
                {
                    main_IMG_aliens[(i * colLen) + j].setVisibility(View.VISIBLE);
                    main_IMG_metals[(i * colLen) + j].setVisibility(View.INVISIBLE);
                }
                else if (newMat[i][j] == 2)
                {
                    main_IMG_aliens[(i * colLen) + j].setVisibility(View.INVISIBLE);
                    main_IMG_metals[(i * colLen) + j].setVisibility(View.VISIBLE);
                }
                else
                {
                    main_IMG_aliens[(i * colLen) + j].setVisibility(View.INVISIBLE);
                    main_IMG_metals[(i * colLen) + j].setVisibility(View.INVISIBLE);
                }
            }
        }
        for (int i = 0; i < colLen; i++)
        {
            if (newMat[rowLen - 1][i] == 0)
            {
                main_IMG_aliens[((rowLen - 1) * colLen) + i].setVisibility(View.INVISIBLE);
                main_IMG_metals[((rowLen - 1) * colLen) + i].setVisibility(View.INVISIBLE);
            }
            else if (i != shipIndex || checkHit() == 0)
            {
                if (newMat[rowLen - 1][i] == 1) {
                    main_IMG_metals[((rowLen - 1) * colLen) + i].setVisibility(View.INVISIBLE);
                    main_IMG_aliens[((rowLen - 1) * colLen) + i].setVisibility(View.VISIBLE);
                }
                else {
                    main_IMG_aliens[((rowLen - 1) * colLen) + i].setVisibility(View.INVISIBLE);
                    main_IMG_metals[((rowLen - 1) * colLen) + i].setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void findViews() {
        main_BTN_left = findViewById(R.id.main_BTN_left);
        main_BTN_right = findViewById(R.id.main_BTN_right);
        main_IMG_hearts = new AppCompatImageView[]{
                findViewById(R.id.main_IMG_heart1),
                findViewById(R.id.main_IMG_heart2),
                findViewById(R.id.main_IMG_heart3)
        };
        main_IMG_ships = new AppCompatImageView[]{
                findViewById(R.id.space_ship_7_0),
                findViewById(R.id.space_ship_7_1),
                findViewById(R.id.space_ship_7_2),
                findViewById(R.id.space_ship_7_3),
                findViewById(R.id.space_ship_7_4)
        };
        main_IMG_aliens = new AppCompatImageView[]{
                findViewById(R.id.alien_0_0), findViewById(R.id.alien_0_1),
                findViewById(R.id.alien_0_2), findViewById(R.id.alien_0_3),
                findViewById(R.id.alien_0_4), findViewById(R.id.alien_1_0),
                findViewById(R.id.alien_1_1), findViewById(R.id.alien_1_2),
                findViewById(R.id.alien_1_3), findViewById(R.id.alien_1_4),
                findViewById(R.id.alien_2_0), findViewById(R.id.alien_2_1),
                findViewById(R.id.alien_2_2), findViewById(R.id.alien_2_3),
                findViewById(R.id.alien_2_4), findViewById(R.id.alien_3_0),
                findViewById(R.id.alien_3_1), findViewById(R.id.alien_3_2),
                findViewById(R.id.alien_3_3), findViewById(R.id.alien_3_4),
                findViewById(R.id.alien_4_0), findViewById(R.id.alien_4_1),
                findViewById(R.id.alien_4_2), findViewById(R.id.alien_4_3),
                findViewById(R.id.alien_4_4), findViewById(R.id.alien_5_0),
                findViewById(R.id.alien_5_1), findViewById(R.id.alien_5_2),
                findViewById(R.id.alien_5_3), findViewById(R.id.alien_5_4),
                findViewById(R.id.alien_6_0), findViewById(R.id.alien_6_1),
                findViewById(R.id.alien_6_2), findViewById(R.id.alien_6_3),
                findViewById(R.id.alien_6_4), findViewById(R.id.alien_7_0),
                findViewById(R.id.alien_7_1), findViewById(R.id.alien_7_2),
                findViewById(R.id.alien_7_3), findViewById(R.id.alien_7_4)
        };
        main_IMG_metals = new AppCompatImageView[]{
                findViewById(R.id.metal_bar0_0), findViewById(R.id.metal_bar0_1),
                findViewById(R.id.metal_bar0_2), findViewById(R.id.metal_bar0_3),
                findViewById(R.id.metal_bar0_4), findViewById(R.id.metal_bar1_0),
                findViewById(R.id.metal_bar1_1), findViewById(R.id.metal_bar1_2),
                findViewById(R.id.metal_bar1_3), findViewById(R.id.metal_bar1_4),
                findViewById(R.id.metal_bar2_0), findViewById(R.id.metal_bar2_1),
                findViewById(R.id.metal_bar2_2), findViewById(R.id.metal_bar2_3),
                findViewById(R.id.metal_bar2_4), findViewById(R.id.metal_bar3_0),
                findViewById(R.id.metal_bar3_1), findViewById(R.id.metal_bar3_2),
                findViewById(R.id.metal_bar3_3), findViewById(R.id.metal_bar3_4),
                findViewById(R.id.metal_bar4_0), findViewById(R.id.metal_bar4_1),
                findViewById(R.id.metal_bar4_2), findViewById(R.id.metal_bar4_3),
                findViewById(R.id.metal_bar4_4), findViewById(R.id.metal_bar5_0),
                findViewById(R.id.metal_bar5_1), findViewById(R.id.metal_bar5_2),
                findViewById(R.id.metal_bar5_3), findViewById(R.id.metal_bar5_4),
                findViewById(R.id.metal_bar6_0), findViewById(R.id.metal_bar6_1),
                findViewById(R.id.metal_bar6_2), findViewById(R.id.metal_bar6_3),
                findViewById(R.id.metal_bar6_4), findViewById(R.id.metal_bar7_0),
                findViewById(R.id.metal_bar7_1), findViewById(R.id.metal_bar7_2),
                findViewById(R.id.metal_bar7_3), findViewById(R.id.metal_bar7_4)
        };
        main_LBL_score = findViewById(R.id.main_LBL_score);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        soundPlayer.stopSound();
        stopTimer();
        if (GAME_MODE.equals("SENSOR"))
            moveDetector.stop();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        soundPlayer = new SoundPlayer(this);
        soundPlayer.playSound(true, R.raw.space_traveller);
        startTimer();
        if (GAME_MODE.equals("SENSOR"))
            moveDetector.start();
    }
}