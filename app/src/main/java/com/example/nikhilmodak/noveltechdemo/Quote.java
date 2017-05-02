package com.example.nikhilmodak.noveltechdemo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nikhil Modak on 4/18/2017.
 */

public class Quote {

    private String quote;
    private String quoteID;
    private String speaker;
    private HashMap<String, String> likes = new HashMap<>();

    public Quote() {
        quote = "sup";
        speaker = "hey";
        quoteID = "howdy";
    }

    public Quote(String quote, String speaker, String quoteID) {
        this.quote = quote;
        this.speaker = speaker;
        this.quoteID= quoteID;
        //this.author = author;
    }

    public String getQuote() {
        return quote;
    }

    public String getSpeaker() {
        return speaker;
    }

    public String getQuoteID() {
        return quoteID;
    }

    public HashMap<String, String> getLikes() {
        return likes;
    }


}
