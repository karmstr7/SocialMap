package com.example.quinnm.socialmap.api.model;

import java.util.ArrayList;

public class Registration {
    private String error_msg;
    private String user_id;
    private ArrayList<String> friends;
    private String date_created;
    private String username;
    private String password;

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