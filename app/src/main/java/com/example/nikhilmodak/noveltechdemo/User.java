package com.example.nikhilmodak.noveltechdemo;

/**
 * Created by Nikhil Modak on 4/18/2017.
 */

public class User {

    private String name;
    private String email;

    public User() {

        name = "name";
        email = "email";
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

}
