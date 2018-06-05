package com.example.quinnm.socialmap.api.model;

public class DeleteUser {
    private String user_id;
    private String error_msg;

    public DeleteUser(String userId) {
        this.user_id = userId;
    }

    public String getErrorMsg() {
        return error_msg;
    }
}
