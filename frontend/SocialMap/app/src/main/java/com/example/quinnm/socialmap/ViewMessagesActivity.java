package com.example.quinnm.socialmap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

/**
 * The main view for displaying the message list. *
 * Contains a RecyclerView
 * Comes from MainActivity.
 *
 * @author Keir Armstrong
 * @since June 4, 2018
 */

public class ViewMessagesActivity extends AppCompatActivity {
    private static final String TAG = "ViewMEssagesActivity";

    private List<Map<String, Object>> _messages;
    private String _username;

    private RecyclerView _recyclerView;
    private MessageListRecyclerViewAdapter _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_messages);

        _recyclerView = findViewById(R.id.viewMessage_recyclerView);

        this._messages = ((ApplicationStore) this.getApplication()).getMessages();
        this._username = ((ApplicationStore) this.getApplication()).getUsername();

        initRecyclerView();
    }

    private void initRecyclerView() {
        _adapter = new MessageListRecyclerViewAdapter(filterFriendMessages(), _username, this);
        _recyclerView.setAdapter(_adapter);
        _recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private List<Map<String, Object>> filterFriendMessages() {
        List<Map<String, Object>> copyList = new ArrayList<>();

        int listSize = _messages.size();

        for (int i = 0; i < listSize; i++) {
            if (_messages.get(i).get("username").equals(_username)) {
                copyList.add(_messages.get(i));
            }
        }

        return copyList;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
