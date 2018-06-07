package com.example.quinnm.socialmap;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quinnm.socialmap.api.model.AddFriend;
import com.example.quinnm.socialmap.api.service.FriendsListClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class FriendListRecyclerViewAdapter extends RecyclerView.Adapter<FriendListRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "FriendListRecyclerViewAdapter";

    private List<String> _friends;
    private Context _context;
    private String _username;

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView friendName;
        Button deleteFriend;
        RelativeLayout parentLayout;

        ViewHolder(View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.friend_name);
            deleteFriend = itemView.findViewById(R.id.deleteButton);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }

    FriendListRecyclerViewAdapter(String username, List<String> friends, Context context) {
        this._username = username;
        this._friends = friends;
        this._context = context;
    }

    private void deleteFriend(@NonNull ViewHolder holder) {
        String targetFriend = _friends.get(holder.getAdapterPosition());

        AddFriend oldFriend = new AddFriend(
                _username,
                targetFriend
        );

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(_context.getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        FriendsListClient client = retrofit.create(FriendsListClient.class);
        Call<AddFriend> call = client.delFriend(oldFriend);

        call.enqueue(new Callback<AddFriend>() {
            @Override
            public void onResponse(@NonNull Call<AddFriend> call, @NonNull Response<AddFriend> response) {
                if (response.body() != null && response.body().getErrorMsg().equals("")) {
                    Toast.makeText(_context.getApplicationContext(), "Friend deleted", Toast.LENGTH_SHORT).show();
                    onDeleteFriendSuccess(holder);
                }
                else {
                    Toast.makeText(_context.getApplicationContext(), "ERROR: " + response.body().getErrorMsg(), Toast.LENGTH_SHORT).show();
                    onDeleteFriendFailure();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AddFriend> call, @NonNull Throwable t) {
                Toast.makeText(_context.getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
                onDeleteFriendFailure();
            }
        });
    }

    public void addFriend(String friendName) {
        _friends.add(friendName);
        notifyItemInserted(_friends.size() + 1);
    }

    // change this file name

    private void onDeleteFriendSuccess(@NonNull ViewHolder holder) {
        ((ApplicationStore) _context.getApplicationContext()).decrementNumberOfFriends();
        _friends.remove(holder.getAdapterPosition());
        notifyItemRemoved(holder.getAdapterPosition());
    }

    private void onDeleteFriendFailure() {
        // do stuff here?
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listfriends,parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.friendName.setText(_friends.get(position));
        holder.deleteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFriend(holder);
            }
        });

    }

    @Override
    public int getItemCount() {
        return _friends.size();
    }
}