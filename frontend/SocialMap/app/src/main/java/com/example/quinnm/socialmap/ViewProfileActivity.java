package com.example.quinnm.socialmap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ViewProfileActivity extends AppCompatActivity {
    private TextView _usernameTextView, _accountDateTextView, _numberMessagesTextView, _numberFriendsTextView;
    private Button _deleteAccountButton, _signOutButton;

    private String _username, _accountDate;
    private int _numberOfMessages, _numberOfFriends;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        _usernameTextView = findViewById(R.id.viewProfile_showUsername);
        _accountDateTextView = findViewById(R.id.viewProfile_showAccountDate);
        _numberMessagesTextView = findViewById(R.id.viewProfile_showNumberMessages);
        _numberFriendsTextView = findViewById(R.id.viewProfile_showNumberFriends);

        _deleteAccountButton = findViewById(R.id.viewProfile_deleteAccount);
        _signOutButton = findViewById(R.id.viewProfile_signOut);

        _deleteAccountButton.setOnClickListener(
                (View v) -> onDeleteClick()
        );

        _signOutButton.setOnClickListener(
                (View v) -> onSignOutClick()
        );

        _username = ((ApplicationStore) this.getApplication()).getUsername();
        _accountDate = ((ApplicationStore) this.getApplication()).getDateCreated();
        _numberOfMessages = ((ApplicationStore) this.getApplication()).getNumberOfFriends();
        _numberOfFriends = ((ApplicationStore) this.getApplication()).getNumberOfFriends();

        displayProfileValues();
    }

    private void displayProfileValues() {
        _usernameTextView.setText(getString(R.string.viewProfile_showUsername, _username));
        _accountDateTextView.setText(getString(R.string.viewProfile_showAccountDate, _accountDate));
        _numberMessagesTextView.setText(getString(R.string.viewProfile_showNumberMessages, _numberOfMessages));
        _numberFriendsTextView.setText(getString(R.string.viewProfile_showNumberFriends, _numberOfFriends));
    }

    private void onDeleteClick() {

    }

    private void onSignOutClick() {

    }
}
