package com.example.quinnm.socialmap.api.service;
import com.example.quinnm.socialmap.api.model.FriendsList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface FriendsListClient {
    @GET("getFriendsList")
    Call<FriendsList> getFriendsList(@Body String username);

}
