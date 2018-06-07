package com.example.quinnm.socialmap.api.model;

import java.util.List;
import java.util.Map;

/**
 * This class is used to carry data in the Http request to the server.
 * Get messages with username and a friend list as parameters.
 * The retrieved messages are stored in result.
 *
 * @author Keir Armstrong
 * @since June 1, 2018
 */

public class GetMessage {
    // sending...
    private List<String> friends;
    private String username;

    // receiving...
    private List<Map<String, Object>> result;
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
