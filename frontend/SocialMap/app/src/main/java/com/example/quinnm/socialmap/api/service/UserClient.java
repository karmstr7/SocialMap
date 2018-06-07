package com.example.quinnm.socialmap.api.service;

import com.example.quinnm.socialmap.api.model.DeleteUser;
import com.example.quinnm.socialmap.api.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Interface to be used by Retrofit for Http requests
 * Access data models to log in and delete user;
 */

public interface UserClient {
    @POST("login")
    Call<User> loginAccount(@Body User user);

    @POST("delUser")
    Call<DeleteUser> deleteAccount(@Body DeleteUser deleteUser);
}
