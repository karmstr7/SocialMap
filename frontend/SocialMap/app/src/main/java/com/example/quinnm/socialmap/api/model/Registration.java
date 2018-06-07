package com.example.quinnm.socialmap.api.model;

import java.util.ArrayList;

/**
 * This class is used to carry data in the Http request to the server.
 * Arguments: username, password, date of creation
 * Returns: error message, user id, friends list
 *
 * @author Keir Armstrong
 * @since June 1, 2018
 */


public class Registration {
    // sending...
    private String date_created;
    private String username;
    private String password;

    // receiving...
    private String error_msg;
    private String user_id;
    private ArrayList<String> friends;

    public Registration(String username, String password, String date_created) {
        this.username = username;
        this.password = password;
        this.date_created = date_created;
    }

    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return user_id;
    }

    public String getDateCreated() {
        return date_created;
    }

    public String getErrorMsg() {
        return error_msg;
    }
}
