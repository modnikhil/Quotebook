package com.example.nikhilmodak.noveltechdemo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class represents a single quote that has been submitted by
 * a user. It contains the quote itself, the person who said it,
 * a unique Firebase identifier and the number of likes it has
 * Created by Nikhil Modak on 4/18/2017.
 */

public class Quote {

    private String quote;
    private String quoteID;
    private String speaker;
    private HashMap<String, String> likes;

    public Quote() {
        quote = "Quote";
        speaker = "Geoff";
        quoteID = "ID";
        likes = new HashMap<>();
    }

    public Quote(String quote, String speaker, String quoteID) {
        this.quote = quote;
        this.speaker = speaker;
        this.quoteID= quoteID;
        likes = new HashMap<>();
    }

    /**
     * Returns the actual quote this object represents
     * @return the quote of the object
     */
    public String getQuote() {
        return quote;
    }

    /**
     * Returns the speaker of the quote
     * @return the speaker of the quote
     */
    public String getSpeaker() {
        return speaker;
    }

    /**
     * Returns the unique Firebase ID of the quote
     * @return the Firebase ID of the quote
     */
    public String getQuoteID() {
        return quoteID;
    }

    /**
     * Returns the list of userIDs who have liked the quote
     * @return list of users who liked the quote
     */
    public HashMap<String, String> getLikes() {
        return likes;
    }

}
