package com.example.maxapp1.Utillities;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.maxapp1.Interfaces.MoveCallback;

public class MoveDetector {
    private final SensorManager sensorManager;
    private final Sensor sensor;
    private SensorEventListener sensorEventListener;


    private long timestamp = 0L;

    private final MoveCallback moveCallback;

    public MoveDetector(Context context, MoveCallback moveCallback) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.moveCallback = moveCallback;
        initEventListener();
    }

    private void initEventListener() {
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                calculateMove(x, y);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // pass
            }
        };
    }

    private void calculateMove(float x, float y) {
        if (System.currentTimeMillis() - timestamp > 200) {
            timestamp = System.currentTimeMillis();
            if (x > 3.0) {
                if (moveCallback != null)
                    moveCallback.movePosX();
            }

            if (x < -3.0) {
                if (moveCallback != null)
                    moveCallback.moveNegX();
            }

            if (y > 1.0) {
                if (moveCallback != null)
                    moveCallback.movePosY();
            }

            if (y < -1.0) {
                if (moveCallback != null)
                    moveCallback.moveNegY();
            }
        }
    }

    public void start() {
        sensorManager.registerListener(
                sensorEventListener,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
        );
    }

    public void stop() {
        sensorManager.unregisterListener(
                sensorEventListener,
                sensor
        );
    }
}