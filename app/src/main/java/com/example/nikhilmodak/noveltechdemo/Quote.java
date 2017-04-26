package com.example.nikhilmodak.noveltechdemo;

/**
 * Created by Nikhil Modak on 4/18/2017.
 */

public class Quote {

    private String quote;
    private String speaker;
    private User author;

    public Quote() {
        quote = "sup";
        speaker = "hey";
    }

    public Quote(String quote, String speaker) {
        this.quote = quote;
        this.speaker = speaker;
        //this.author = author;
    }

    public String getQuote() {
        return quote;
    }

    public String getSpeaker() {
        return speaker;
    }

    public User getAuthor() {
        return author;
    }
}
