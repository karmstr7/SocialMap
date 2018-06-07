package com.example.quinnm.socialmap.api.model;

/**
 * This class is used to carry data in the Http request to the server.
 * Put messages to the database using username, message text, and message data
 * as parameters.
 * The retrieved object is the message's id.
 *
 * @author Keir Armstrong
 * @since June 1, 2018
 */

public class Message {
    // sending...
    private String username;
    private String msg_body;
    private String msg_data;

    // receiving...
    private String message_id;


    public Message(String username, String message, String location) {
        this.username = username;
        this.msg_body = message;
        this.msg_data = location;
    }
    public String getId() {
        return message_id;
    }
}

