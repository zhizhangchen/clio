package com.clio.exercise.johnchen.matters.storage;

import android.content.Context;
/**
 * Created by user on 2015/9/26.
 * The factory class to create a Storage instance. Future implementation of Storage
 * can be created here without modifying other files
 */
public class StorageFactory {
    public static Storage getStorage(Context context) {
        return new SharedPreferencesStorage(context);
    }
}
