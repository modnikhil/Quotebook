package com.example.nikhilmodak.noveltechdemo;

import java.util.ArrayList;

/**
 * Created by Nikhil Modak on 4/18/2017.
 */

public class Group {

    private ArrayList<User> users;
    private ArrayList<Quote> quotes;

    public Group(ArrayList<User> users, ArrayList<Quote> quotes) {
        this.users = users;
        this.quotes = quotes;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList<Quote> getQuotes() {
        return quotes;
    }
}
