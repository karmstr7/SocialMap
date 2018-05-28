package com.example.quinnm.socialmap.api.service;

import com.example.quinnm.socialmap.api.model.Message;
import com.example.quinnm.socialmap.api.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MessageClient {
    @POST("addMsg")
    Call<Message> addMessage (@Body Message message);

    @POST("delMsg")
    Call<Message> deleteMessage(@Body Message message);

    @POST("getMsgs")
    Call<Message> getMessages(@Body User user);

}
