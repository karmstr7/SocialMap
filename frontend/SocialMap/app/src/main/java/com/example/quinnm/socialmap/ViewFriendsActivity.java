package com.example.quinnm.socialmap;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.quinnm.socialmap.api.model.AddFriend;
import com.example.quinnm.socialmap.api.model.FriendsList;
import com.example.quinnm.socialmap.api.model.User;
import com.example.quinnm.socialmap.api.service.FriendsListClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewFriendsActivity extends AppCompatActivity implements AddFriendFragment.AddFriendDialogListener{

    private static final String TAG = "ViewFriendsActivity";

    List<String> friends;
    String username;
    Button _addFriendButton;
    Button _doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friends);
        username = ((ApplicationStore) this.getApplication()).getUsername();
        getFriends();
        _addFriendButton = findViewById(R.id._addFriendButton);
        _doneButton = findViewById(R.id._done);


        _addFriendButton.setOnClickListener(
                (View v) -> onAddFriend());

        _doneButton.setOnClickListener(
                (View v) -> onDoneClick()
        );
    }


    private void onDoneClick() {
        setResult(RESULT_OK, null);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        ViewFriendsActivity.this.startActivity(intent);
        finish();
    }
    public void onAddFriend() {
        FragmentManager fm = getSupportFragmentManager();
        AddFriendFragment newAddFriendFragment = AddFriendFragment.newInstance("Title");
        newAddFriendFragment.show(fm, "AddFriendFragment");
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: INIT RECYCLERVIEW");
        RecyclerView recyclerView = (findViewById(R.id.recycler_view));
        Log.d(TAG, "initRecyclerView: SHOWING FRIENDSLIST: " +friends);

        RecyclerViewAdapter adapter = new RecyclerViewAdapter((ArrayList<String>) friends,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void getFriends() {
        FriendsList friendsList = new FriendsList(username);
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/socialmap/api/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        FriendsListClient client = retrofit.create(FriendsListClient.class);
        Call<FriendsList> call = client.getFriendsList(friendsList);

        call.enqueue(new Callback<FriendsList>() {
            @Override
            public void onResponse(Call<FriendsList> call, Response<FriendsList> response) {
                Toast.makeText(getBaseContext(),"ON RESPONSE",Toast.LENGTH_LONG);
                Log.d(TAG, "onResponse: PRINTING RESPONSE body:" + response.body().getFriends());
                if (response.body() != null) {
//                    Toast.makeText(getBaseContext(),"GETFRIENDS: GOT RESPONSE",Toast.LENGTH_LONG).show();
                    friends = response.body().getFriends();
                } else {
                    Toast.makeText(getBaseContext(),
                            "ERROR: " + response.body().getErrorMsg(),
                            Toast.LENGTH_LONG).show();
                }
                initRecyclerView();
            }

            @Override
            public void onFailure(Call<FriendsList> call, Throwable t) {
                Toast.makeText(getBaseContext(),"ON FAILURE",Toast.LENGTH_LONG);
                Toast.makeText(getBaseContext(), t.toString(), Toast.LENGTH_LONG).show();
            }

        });
    }

//    Method to remove friend on the server
    public void deleteFriend(String Exfriend) {
        AddFriend oldFriend = new AddFriend(username, Exfriend);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/socialmap/api/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        FriendsListClient client = retrofit.create(FriendsListClient.class);
        Call<AddFriend> call = client.delFriend(oldFriend);

        call.enqueue(new Callback<AddFriend>() {
            @Override
            public void onResponse(Call<AddFriend> call, Response<AddFriend> response) {
                if (response.body() != null && response.body().getErrorMsg().equals("")) {
                    Toast.makeText(getBaseContext(),
                            "Friend Removed", Toast.LENGTH_SHORT).show();
//                    new call to get friends, which refreshes the list of friends
                    getFriends();

//                    update friends list. FIXME
                    updateFriends(friends);
                }
            }
            @Override
            public void onFailure(Call<AddFriend> call, Throwable t) {
                Toast.makeText(getBaseContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
        public void updateFriends(List<String> friends) {
            ((ApplicationStore) this.getApplication()).setFriends(friends);
        }



    @Override
    public void OnAddFriend(String friend){
        FragmentManager fm = getSupportFragmentManager();
        AddFriendFragment addFriendDialogFragment = AddFriendFragment.newInstance("Some Title");
        addFriendDialogFragment.show(fm, "addFriendDialogFragment");


        AddFriend newFriend = new AddFriend(username, friend);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/socialmap/api/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        FriendsListClient client = retrofit.create(FriendsListClient.class);
        Call<AddFriend> call = client.addFriend(newFriend);

        call.enqueue(new Callback<AddFriend>() {
            @Override
            public void onResponse(Call<AddFriend> call, Response<AddFriend> response) {

                if (response.body() != null && response.body().getErrorMsg().equals("")){
                    Toast.makeText(getBaseContext(),
                            "Friend Added",Toast.LENGTH_SHORT).show();
                    }
                else{
                Toast.makeText(getBaseContext(),
                        "ERROR: " + response.body().getErrorMsg(),
                        Toast.LENGTH_LONG).show();
                }
                getFriends();
            }

            @Override
            public void onFailure(Call<AddFriend> call, Throwable t) {
                Toast.makeText(getBaseContext(), t.toString(), Toast.LENGTH_LONG).show();


            }
        });

    }
}








