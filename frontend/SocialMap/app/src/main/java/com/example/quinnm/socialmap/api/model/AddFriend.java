package com.example.quinnm.socialmap.api.model;


/**
 * This class is used to carry data in the Http request to the server.
 * Arguments: username, friend name
 * Returns: error message
 *
 * @author Keir Armstrong
 * @since June 1, 2018
 */
public class AddFriend {
    // sending...
    private String username;
    private String friend;

    // receiving...
    private String error_msg;

    // get the value from the server
    public String getErrorMsg(){
        return error_msg;
    }

    // get the values to send to the server
    public AddFriend(String username, String friend){
        this.username = username;
        this.friend = friend;
    }

}
