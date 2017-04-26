package com.example.nikhilmodak.noveltechdemo;

import java.util.ArrayList;

/**
 * Created by Nikhil Modak on 4/18/2017.
 */

public class Group {

    private String name;
    private String groupID;
    private ArrayList<User> users;
    private ArrayList<Quote> quotes;

    public Group() {
        name = "rgrt";
        groupID = "Hello";
        users = null;
        quotes = null;
    }

    public Group(String name, String groupID, ArrayList<User> users, ArrayList<Quote> quotes) {
        this.name = name;
        this.groupID = groupID;
        this.users = users;
        this.quotes = quotes;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList<Quote> getQuotes() {
        return quotes;
    }

    public String getName() {
        return name;
    }

    public String getGroupID() {
        return groupID;
    }
}
