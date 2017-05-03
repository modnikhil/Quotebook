package com.example.nikhilmodak.noveltechdemo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * This class represents a group object that contains
 * all the quotes and also keeps track of which users are
 * a part of the group. Each group has a unique ID in the
 * Firebase Realtime Database.
 * Created by Nikhil Modak on 4/18/2017.
 */
public class Group {

    private String name;
    private String groupID;

    public Group() {
        name = "name";
        groupID = "ID";
    }

    public Group(String name, String groupID) {
        this.name = name;
        this.groupID = groupID;
    }


    /**
     * Returns the name of the group
     * @return name of group
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the Firebase ID of the group
     * @return ID of group
     */
    public String getGroupID() {
        return groupID;
    }



}
