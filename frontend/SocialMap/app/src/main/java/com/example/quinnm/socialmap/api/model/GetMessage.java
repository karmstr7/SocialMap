package com.example.quinnm.socialmap.api.model;

import java.util.List;
import java.util.Map;

public class GetMessage {
    private List<String> friends;
    private List<Map<String, Object>> result;
    private String username;
    private String error_msg;

    public GetMessage(String username, List<String> friends) {
        this.username = username;
        this.friends = friends;
    }

    public List<Map<String, Object>> getMessages() {
        return result;
    }

    public String getErrorMsg() {
        return error_msg;
    }
}
