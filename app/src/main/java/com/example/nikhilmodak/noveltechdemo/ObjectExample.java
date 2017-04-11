package com.example.nikhilmodak.noveltechdemo;

/**
 * Created by Nikhil Modak on 4/10/2017.
 */

public class ObjectExample {
    private String name;
    private int index;
    private String item;

    public ObjectExample(String name, int index, String item) {
        this.name = name;
        this.index = index;
        this.item = item;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public String getItem() {
        return item;
    }
}
