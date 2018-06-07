package com.example.quinnm.socialmap.api.model;

import java.util.ArrayList;

/**
 * This class is used to carry data in the Http request to the server.
 * Gets a the friend list of the user, with the username as the passing parameter.
 * Retrieves a list of friends and an error message.
 *
 * @author Keir Armstrong
 * @since June 1, 2018
 */
public class FriendsList {
    // sending...
    private String username;

    // receiving...
    private String error_msg;
    private ArrayList<String> friends;

    public ArrayList<String> getFriends() {
        return friends;
    }
    public String getErrorMsg() {
        return error_msg;
    }

    public FriendsList(String username){
        this.username = username;
    }
}

