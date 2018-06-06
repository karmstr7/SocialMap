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
        filterFriendMessages();

        _adapter = new MessageListRecyclerViewAdapter(_messages, _username, this);
        _recyclerView.setAdapter(_adapter);
        _recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void filterFriendMessages() {
        List<Map<String, Object>> copyList = new ArrayList<>(_messages);

        int listSize = copyList.size();

        for (int i = 0; i < listSize; i++) {
            if (!copyList.get(i).get("username").equals(_username)) {
                _messages.remove(i);
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
