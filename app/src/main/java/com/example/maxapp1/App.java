package com.example.maxapp1;

import android.app.Application;

import com.example.maxapp1.Utillities.SharedPreferencesManager;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesManager.init(this);
    }
}
