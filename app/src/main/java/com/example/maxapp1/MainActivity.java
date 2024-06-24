package com.example.maxapp1;

import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.maxapp1.Logic.GameManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final long DELAY = 450L;
    private FloatingActionButton main_BTN_left;
    private FloatingActionButton main_BTN_right;

    private GameManager gameManager;
    private AppCompatImageView[] main_IMG_hearts;
    private AppCompatImageView[] main_IMG_asteroids;
    private AppCompatImageView[] main_IMG_ships;

    private Timer timer;
    private boolean timerOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        gameManager = new GameManager(main_IMG_hearts.length);
        initViews();
    }

    private void refreshUI() {
        //Lost:
        if (gameManager.isGameLost()){
            stopTimer();
            gameManager = new GameManager(main_IMG_hearts.length);
            setVisibility();
            startTimer();
        }
        //GAME ON!
        else{
            Random rand = new Random();
            moveNext(rand.nextInt(3));
        }
    }

    private void initViews() {
        main_BTN_left.setOnClickListener( v -> answerClicked(0));
        main_BTN_right.setOnClickListener( v -> answerClicked(1));
        setVisibility();
    }

    private void setVisibility()
    {
        for (AppCompatImageView mainImgAsteroid : main_IMG_asteroids)
            mainImgAsteroid.setVisibility(View.INVISIBLE);
        for (AppCompatImageView mainImgShip : main_IMG_ships)
            mainImgShip.setVisibility(View.INVISIBLE);
        main_IMG_ships[1].setVisibility(View.VISIBLE);
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
            }, DELAY,DELAY);
        }
    }

    private void stopTimer() {
        if (timerOn) {
            timerOn = false;
            timer.cancel();
        }
    }

    private void answerClicked(int ans) {
        int index = gameManager.getShipIndex();
        boolean didMove = gameManager.moveShip(ans);
        if (didMove && checkHit())
            main_IMG_asteroids[((gameManager.getLogicMat().length - 1) * main_IMG_ships.length) + index].setVisibility(View.INVISIBLE);
        if (didMove && ans == 0)
        {
            main_IMG_ships[index].setVisibility(View.INVISIBLE);
            main_IMG_ships[index - 1].setVisibility(View.VISIBLE);
        }
        else if (didMove && ans == 1){
            main_IMG_ships[index].setVisibility(View.INVISIBLE);
            main_IMG_ships[index + 1].setVisibility(View.VISIBLE);
        }
    }

    private boolean checkHit() {
        if (gameManager.isHit()) {
            main_IMG_hearts[main_IMG_hearts.length - gameManager.getDmg()].setVisibility(View.INVISIBLE);
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(500);
            String msg;
            if (gameManager.isGameLost())
                msg = "Game Over!";
            else
                msg = "Crash!";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void moveNext(int rand)
    {
        boolean[][] astrMat = gameManager.getLogicMat();
        gameManager.moveNext(rand);
        boolean[][] newMat = gameManager.getLogicMat();
        for (int i = 0; i < astrMat.length; i++)
        {
            for (int j = 0; j < astrMat[i].length; j++)
            {
                if (astrMat[i][j] && !newMat[i][j])
                        main_IMG_asteroids[(i * astrMat[0].length) + j].setVisibility(View.INVISIBLE);
                else if (!astrMat[i][j] && newMat[i][j])
                {
                    if ((i != (astrMat.length - 1)) || !checkHit())
                        main_IMG_asteroids[(i * astrMat[0].length) + j].setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void findViews() {
        main_BTN_left = findViewById(R.id.main_BTN_left);
        main_BTN_right = findViewById(R.id.main_BTN_right);
        /*toast_BTN_toastAndVibrate = findViewById(R.id.toast_BTN_toastAndVibrate);*/
        main_IMG_hearts = new AppCompatImageView[]{
                findViewById(R.id.main_IMG_heart1),
                findViewById(R.id.main_IMG_heart2),
                findViewById(R.id.main_IMG_heart3)
        };
        main_IMG_ships = new AppCompatImageView[]{
                findViewById(R.id.space_ship_4_0),
                findViewById(R.id.space_ship_4_1),
                findViewById(R.id.space_ship_4_2)
        };
        main_IMG_asteroids = new AppCompatImageView[]{
                findViewById(R.id.asteroid_0_0),
                findViewById(R.id.asteroid_0_1),
                findViewById(R.id.asteroid_0_2),
                findViewById(R.id.asteroid_1_0),
                findViewById(R.id.asteroid_1_1),
                findViewById(R.id.asteroid_1_2),
                findViewById(R.id.asteroid_2_0),
                findViewById(R.id.asteroid_2_1),
                findViewById(R.id.asteroid_2_2),
                findViewById(R.id.asteroid_3_0),
                findViewById(R.id.asteroid_3_1),
                findViewById(R.id.asteroid_3_2),
                findViewById(R.id.asteroid_4_0),
                findViewById(R.id.asteroid_4_1),
                findViewById(R.id.asteroid_4_2)
        };
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        stopTimer();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        startTimer();
    }
}