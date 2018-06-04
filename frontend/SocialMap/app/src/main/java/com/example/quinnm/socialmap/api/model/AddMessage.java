package com.example.quinnm.socialmap.api.model;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class AddMessage {
    // sending...
    String username;
    String msg_body;
    LatLng msg_data;
    // receiving...
    String error_msg;
    String message_id;

    public AddMessage(String username, String message_body, LatLng message_data) {
        this.username = username;
        this.msg_body = message_body;
        this.msg_data = message_data;
    }

    public String getErrorMsg() {
        return error_msg;
    }

    public String getMessageId() {
        return message_id;
    }

    public String getMessageBody() {
        return msg_body;
    }

    public LatLng getMessageData() {
        return msg_data;
    }
}
