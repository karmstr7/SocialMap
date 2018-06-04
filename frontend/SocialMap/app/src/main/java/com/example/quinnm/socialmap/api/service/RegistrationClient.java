package com.example.quinnm.socialmap.api.service;

import com.example.quinnm.socialmap.api.model.Registration;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegistrationClient {
    @POST("signup")
    Call<Registration> createAccount(@Body Registration registration);
}
