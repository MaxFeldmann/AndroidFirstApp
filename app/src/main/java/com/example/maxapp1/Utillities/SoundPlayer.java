package com.example.maxapp1.Utillities;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SoundPlayer {
    private final Context context;
    private final Executor executor;
    private MediaPlayer mediaPlayer;

    public SoundPlayer(Context context) {
        this.context = context;
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void playSound(boolean isLoop, int resID) {
        if (mediaPlayer == null){
            executor.execute(() -> {
                mediaPlayer = MediaPlayer.create(context, resID);
                mediaPlayer.setLooping(isLoop);
                mediaPlayer.setVolume(1.0f,1.0f);
                mediaPlayer.start();
            });
        }
    }

    public void stopSound() {
        if (mediaPlayer != null){
            executor.execute(() -> {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            });
        }
    }
}
