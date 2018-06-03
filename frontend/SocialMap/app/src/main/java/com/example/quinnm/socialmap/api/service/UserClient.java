package com.example.quinnm.socialmap.api.service;

import com.example.quinnm.socialmap.api.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserClient {
    @POST("login")
    Call<User> loginAccount(@Body User user);

    @POST("signup")
    Call<User> createAccount(@Body User user);

    @POST("getFriendsList")
    Call<User> getFriendsList(@Body User user);

    @POST("addFriend")
    Call<User> addFriend(@Body User user);

    @POST("defFriend")
    Call<User> delFriend(@Body User user);



}
