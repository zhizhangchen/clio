package com.clio.exercise.johnchen.matters.storage;

/**
 * Created by user on 2015/9/26.
 */
public interface Storage {
    void setItem(String key, String value);
    String getItem(String key, String defaultValue);
}
