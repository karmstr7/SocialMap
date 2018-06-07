package com.example.quinnm.socialmap.api.model;

/**
 * This class is used to carry data in the Http request to the server.
 * Argument: a message id
 * Return: error message
 *
 * @author Keir Armstrong
 * @since June 1, 2018
 */

public class DeleteMessage {
    // sending...
    private String message_id;

    // receiving...
    private String error_msg;

    public DeleteMessage(String message_id) {
        this.message_id = message_id;
    }

    public String getErrorMsg() {
        return error_msg;
    }
}
