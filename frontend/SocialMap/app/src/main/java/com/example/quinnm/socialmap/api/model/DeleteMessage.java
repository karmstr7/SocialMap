package com.example.quinnm.socialmap.api.model;

public class DeleteMessage {
    private String message_id;
    private String error_msg;

    public DeleteMessage(String message_id) {
        this.message_id = message_id;
    }

    public String getErrorMsg() {
        return error_msg;
    }
}
