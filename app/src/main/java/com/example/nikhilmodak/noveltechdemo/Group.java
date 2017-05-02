package com.example.nikhilmodak.noveltechdemo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Nikhil Modak on 4/18/2017.
 */

public class Group implements Parcelable{

    private String name;
    private String groupID;
    private ArrayList<User> users;
    private ArrayList<Quote> quotes;

    public Group() {
        name = "name";
        groupID = "ID";
        users = null;
        quotes = null;
    }

    public Group(String name, String groupID) {
        this.name = name;
        this.groupID = groupID;
        this.users = null;
        this.quotes = null;
    }

    protected Group(Parcel in) {
        name = in.readString();
        groupID = in.readString();
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(groupID);
    }
}
