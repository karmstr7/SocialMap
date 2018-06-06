package com.example.quinnm.socialmap;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

public class ViewFriendsActivity extends AppCompatActivity implements
    AddFriendDialogFragment.AddFriendDialogListener {
    private static final String TAG = "ViewFriendsActivity";

    private List<String> friends;
    private String username;

    private FloatingActionButton _actionFab;
    private RecyclerView _recyclerView;
    private FriendListRecyclerViewAdapter _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friends);

        _actionFab = findViewById(R.id.viewFriends_floatingActionButton);
        _recyclerView = findViewById(R.id.viewFriends_recyclerView);

        username = ((ApplicationStore) this.getApplication()).getUsername();
        friends = ((ApplicationStore) this.getApplication()).getFriends();

        _actionFab.setOnClickListener(
                (View v) -> onActionFab()
        );

        initRecyclerView();
    }

    private void initRecyclerView() {
        _adapter = new FriendListRecyclerViewAdapter(username, friends, this);
        _recyclerView.setAdapter(_adapter);
        _recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void onActionFab() {
        FragmentManager fm = getSupportFragmentManager();
        AddFriendDialogFragment newAddFriendFragment = AddFriendDialogFragment.newInstance("Title");
        newAddFriendFragment.show(fm, "AddFriendFragment");
    }

    @Override
    public void OnAddFriend(String friendName) {
        _adapter.addFriend(friendName);
    }
}








