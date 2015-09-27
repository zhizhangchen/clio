package com.clio.exercise.johnchen.matters.storage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by user on 2015/9/26.
 */
public class SharedPreferencesStorage implements Storage {
    SharedPreferences mSettings;
    public SharedPreferencesStorage(Context context) {
        mSettings = context.getSharedPreferences("matters", 0);
    }

    @Override
    public void setItem(String key, String value) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    @Override
    public String getItem(String key, String defaultValue) {
        return mSettings.getString(key, defaultValue);
    }
}
