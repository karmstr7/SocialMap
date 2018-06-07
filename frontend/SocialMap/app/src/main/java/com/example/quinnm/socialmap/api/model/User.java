package com.example.quinnm.socialmap.api.model;

import java.util.ArrayList;

/**
 * This class is used to carry data in the Http request to the server.
 * Arguments: username, password
 * Returns: error message, user id, friends list, date of creation
 *
 * @author Keir Armstrong
 * @since June 1, 2018
 */

public class User {
    // sending...
    private String username;
    private String password;

    // receiving...
    private String error_msg;
    private String user_id;
    private ArrayList<String> friends;
    private String date_created;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUserId() {
        return user_id;
    }

    public ArrayList<String> getFriends() {
        return friends;
    }

    public String getDateCreated() {
        return date_created;
    }

    public String getUsername() {
        return username;
    }

    public String getErrorMsg() {
        return error_msg;
    }
}
