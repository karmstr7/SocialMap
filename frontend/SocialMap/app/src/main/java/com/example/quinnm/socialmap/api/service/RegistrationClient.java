package com.example.quinnm.socialmap.api.service;

import com.example.quinnm.socialmap.api.model.Registration;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Interface to be used by Retrofit for Http requests
 * Access data models to register the user
 */

public interface RegistrationClient {
    @POST("signup")
    Call<Registration> createAccount(@Body Registration registration);
}
