package com.example.quinnm.socialmap.api.model;

public class AddFriend {
    private String username;
    private String friend;
    private String error_msg;

    public String getErrorMsg(){
        return error_msg;
    }

    public AddFriend(String username, String friend){
        this.username = username;
        this.friend = friend;
    }

}
