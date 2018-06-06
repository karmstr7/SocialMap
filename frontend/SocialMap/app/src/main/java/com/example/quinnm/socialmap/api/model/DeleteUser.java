package com.example.quinnm.socialmap.api.model;

public class DeleteUser {
    private String username;
    private String error_msg;

    public DeleteUser(String username) {
        this.username = username;
    }

    public String getErrorMsg() {
        return error_msg;
    }
}
