package com.example.quinnm.socialmap.api.service;
import com.example.quinnm.socialmap.api.model.AddFriend;
import com.example.quinnm.socialmap.api.model.FriendsList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface FriendsListClient {
    @POST("getFriends")
    Call<FriendsList> getFriendsList(@Body FriendsList friendsList);

    @POST("addFriend")
    Call<AddFriend> addFriend(@Body AddFriend addFriend);

}
