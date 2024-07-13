package com.example.maxapp1.Utillities;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static volatile SharedPreferencesManager instance = null;
    private final SharedPreferences sharedPref;
    private static final String SP_FILE = "SP_FILE";


    private SharedPreferencesManager(Context context) {
        sharedPref = context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
    }

    public static SharedPreferencesManager getInstance() {
        return instance;
    }

    public static SharedPreferencesManager init(Context context) {
        if (instance == null) {
            synchronized (SharedPreferencesManager.class){
                if (instance == null){
                    instance = new SharedPreferencesManager(context);
                }
            }
        }
        return getInstance();
    }

    public void putString(String key, String value)
    {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String defValue) {
        return sharedPref.getString(key, defValue);
    }
}
