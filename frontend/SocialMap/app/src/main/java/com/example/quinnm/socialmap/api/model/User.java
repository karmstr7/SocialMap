package com.example.quinnm.socialmap.api.model;

import java.util.ArrayList;

public class User {
    private String error_msg;
    private String user_id;
    private ArrayList<String> friends;
    private String date_created;
    private String username;
    private String password;

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
