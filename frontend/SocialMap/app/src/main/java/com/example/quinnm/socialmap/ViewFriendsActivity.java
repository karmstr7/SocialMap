package com.example.quinnm.socialmap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.quinnm.socialmap.api.model.FriendsList;
import com.example.quinnm.socialmap.api.model.Message;
import com.example.quinnm.socialmap.api.model.User;
import com.example.quinnm.socialmap.api.service.FriendsListClient;
import com.example.quinnm.socialmap.api.service.MessageClient;
import com.example.quinnm.socialmap.api.service.UserClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewFriendsActivity extends AppCompatActivity {

    private static final String TAG = "ViewFriendsActivity";

    ArrayList<String> friends;
    String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friends);
        userName = ((ApplicationStore) this.getApplication()).getUsername();
        getFriends();
        initRecyclerView();

    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recylerview");
        RecyclerView recyclerView = (findViewById(R.id.recycler_view));
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(friends,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void getFriends() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/socialmap/api/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        FriendsListClient client = retrofit.create(FriendsListClient.class);
        Call<FriendsList> call = client.getFriendsList(userName);

        call.enqueue(new Callback<FriendsList>() {
            @Override
            public void onResponse(Call<FriendsList> call, Response<FriendsList> response) {
                if (response.body() != null && response.body().getErrorMsg().equals("")) {
                    friends = response.body().getFriends();
                } else {
                    Toast.makeText(getBaseContext(),
                            "ERROR: " + response.body().getErrorMsg(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<FriendsList> call, Throwable t) {
                Toast.makeText(getBaseContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
    public void deleteFriends(){

    }
}






