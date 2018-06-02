package com.example.quinnm.socialmap.api.model;

public class Message {
    private String id;
    private String username;
    private String msg_body;
    private String msg_data;

    public Message(String id, String username, String message, String location) {
        this.id = id;
        this.username = username;
        this.msg_body = message;
        this.msg_data = location;

    }
    public String getId() {
        return id;
    }
}

