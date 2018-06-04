package com.example.quinnm.socialmap;

import android.app.Application;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Application-wide variables are stored here.
 * Each variable has publicly exposed getter and setter methods.
 * To use the store from an Activity, use the following:
 *      // get
 *      ((ApplicationStore) this.getApplication()).setSomeVariable("foo");
 *
 *      // set
 *      String s = ((ApplicationStore) this.getApplication()).getSomeVariable();
 *
 * @author Keir Armstrong
 * @since May 29, 2018
 *
 * REFERENCES:
 *  "Android global variable" - Jeff Gilfelt & PLNech - StackOverflow
 *      https://stackoverflow.com/questions/1944656/android-global-variable
 */

public class ApplicationStore extends Application {
    private String username = "";
    private String userId = "";
    private String dateCreated = "";
    private List<String> friends = new ArrayList<>();
    private List<Map<String, Object>> messages = new ArrayList<>();
    private int numberOfFriends = 0;
    private int numberOfMessages = 0;

    public void setNumberOfMessages(int numberOfMessages) {
        this.numberOfMessages = numberOfMessages;
    }

    public void setNumberOfFriends(int numberOfFriends) {
        this.numberOfFriends = numberOfFriends;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public void addFriend(String friend) {
        this.friends.add(friend);
    }

    public void addMessage(Map<String, Object> message) {
        this.messages.add(message);
    }

    public void increaseFriend() {
        this.numberOfFriends++;
    }

    public void setMessages(List<Map<String, Object>> messages) {
        this.messages = messages;
    }

    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public List<String> getFriends() {
        return friends;
    }

    public List<Map<String, Object>> getMessages() {
        return messages;
    }

    public int getNumberOfFriends() {
        return numberOfFriends;
    }

    public int getNumberOfMessages() {
        return numberOfMessages;
    }
}
