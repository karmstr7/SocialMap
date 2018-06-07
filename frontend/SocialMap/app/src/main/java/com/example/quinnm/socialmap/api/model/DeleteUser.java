package com.example.quinnm.socialmap.api.model;

/**
 * This class is used to carry data in the Http request to the server.
 * Deletes the user with the given username
 * Retrieves an error message.
 *
 * @author Keir Armstrong
 * @since June 1, 2018
 */
public class DeleteUser {
    // sending...
    private String username;

    // receiving...
    private String error_msg;

    public DeleteUser(String username) {
        this.username = username;
    }

    public String getErrorMsg() {
        return error_msg;
    }
}
