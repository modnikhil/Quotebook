package com.example.nikhilmodak.noveltechdemo;

/**
 * Created by Nikhil Modak on 4/18/2017.
 */

public class User {

    private String username;
    private String email;
    private String userID;

    public User() {

        username = "username";
        email = "email";
        userID = "userID";
    }

    public User(String username, String email, String userID) {
        this.username = username;
        this.email = email;
        this.userID = userID;
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

}
