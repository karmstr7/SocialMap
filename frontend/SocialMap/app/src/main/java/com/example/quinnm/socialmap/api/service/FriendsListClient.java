package com.example.quinnm.socialmap.api.service;
import com.example.quinnm.socialmap.api.model.AddFriend;
import com.example.quinnm.socialmap.api.model.FriendsList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Interface to be used by Retrofit for Http requests
 * Access data models to get, add, or delete friends;
 */
public interface FriendsListClient {
    @POST("getFriends")
    Call<FriendsList> getFriendsList(@Body FriendsList friendsList);

    @POST("addFriend")
    Call<AddFriend> addFriend(@Body AddFriend addFriend);

    @POST("delFriend")
    Call<AddFriend> delFriend(@Body AddFriend delFriend);

}
