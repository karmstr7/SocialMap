package com.example.quinnm.socialmap.api.model;

import java.util.ArrayList;

public class FriendsList {
    private String username;
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

