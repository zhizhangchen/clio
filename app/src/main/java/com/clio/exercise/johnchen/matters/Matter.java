package com.clio.exercise.johnchen.matters;

/**
 * Created by user on 2015/9/26.
 */
public class Matter {
    public String id;
    public String displayNumber;
    public String description;
    public Matter(String id, String displayNumber, String description) {
        this.id = id;
        this.displayNumber = displayNumber;
        this.description = description;
    }

    @Override
    public String toString() {
        return displayNumber;
    }
}
