package com.example.nikhilmodak.noveltechdemo;

import java.util.ArrayList;

/**
 * Created by Nikhil Modak on 4/18/2017.
 */

public class User {

    private String username;
    private String email;
    private String userID;
    private ArrayList<Group> groups;

    public User() {
        username = "username";
        email = "email";
        userID = "userID";
        groups = null;
    }

    public User(String username, String email, String userID) {
        this.username = username;
        this.email = email;
        this.userID = userID;
        this.groups = null;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getUserID() {
        return userID;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }
}
