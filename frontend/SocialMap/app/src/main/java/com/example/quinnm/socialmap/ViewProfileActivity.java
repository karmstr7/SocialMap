package com.example.quinnm.socialmap;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quinnm.socialmap.api.model.DeleteUser;
import com.example.quinnm.socialmap.api.service.UserClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * The main view for displaying a summary of the account's details
 * Contains a textView for username
 * Contains a textView for date of creation
 * Contains a textView for number of friends
 * Contains a textView for number of messages
 * Contains a button for delete account
 * Contains a button for sign out
 * Comes from MainActivity.
 *
 * @author Keir Armstrong
 * @since June 4, 2018
 */

public class ViewProfileActivity extends AppCompatActivity {
    // input references
    private TextView _usernameTextView, _accountDateTextView, _numberMessagesTextView, _numberFriendsTextView;
    private Button _deleteAccountButton, _signOutButton;

    // user data
    private String _username, _accountDate;
    private int _numberOfMessages, _numberOfFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create layout
        setContentView(R.layout.activity_view_profile);

        // get input references
        _usernameTextView = findViewById(R.id.viewProfile_showUsername);
        _accountDateTextView = findViewById(R.id.viewProfile_showAccountDate);
        _numberMessagesTextView = findViewById(R.id.viewProfile_showNumberMessages);
        _numberFriendsTextView = findViewById(R.id.viewProfile_showNumberFriends);

        _deleteAccountButton = findViewById(R.id.viewProfile_deleteAccount);
        _signOutButton = findViewById(R.id.viewProfile_signOut);

        // create a click listener for DELETE ACCOUNT button
        _deleteAccountButton.setOnClickListener(
                (View v) -> onDeleteClick()
        );

        // create a click listener for SIGN OUT button
        _signOutButton.setOnClickListener(
                (View v) -> onSignOutClick()
        );

        // get user data from the global variable store
        _username = ((ApplicationStore) this.getApplication()).getUsername();
        _accountDate = ((ApplicationStore) this.getApplication()).getDateCreated();
        _numberOfMessages = getNumberOfMyMessages();
        _numberOfFriends = ((ApplicationStore) this.getApplication()).getNumberOfFriends();

        // display the obtained values by calling this method
        displayProfileValues();
    }

    private int getNumberOfMyMessages() {
        List<Map<String, Object>> messages = ((ApplicationStore) this.getApplication()).getMessages();

        int listSize = messages.size();
        int myMessageTotal = 0;

        for (int i = 0; i < listSize; i++) {
            if (messages.get(i).get("username").equals(_username)) {
                myMessageTotal++;
            }
        }

        return myMessageTotal;
    }

    private void displayProfileValues() {
        // show details about the account
        _usernameTextView.setText(getString(R.string.viewProfile_showUsername, _username));
        _accountDateTextView.setText(getString(R.string.viewProfile_showAccountDate, _accountDate));
        _numberMessagesTextView.setText(getString(R.string.viewProfile_showNumberMessages, _numberOfMessages));
        _numberFriendsTextView.setText(getString(R.string.viewProfile_showNumberFriends, _numberOfFriends));
    }

    private void onDeleteClick() {
        // request for account deletion
        _deleteAccountButton.setEnabled(false);

        DeleteUser deleteUser = new DeleteUser(
                _username
        );

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        UserClient client = retrofit.create(UserClient.class);
        Call<DeleteUser> call = client.deleteAccount(deleteUser);

        call.enqueue(new Callback<DeleteUser>() {
            @Override
            public void onResponse(@NonNull Call<DeleteUser> call, @NonNull Response<DeleteUser> response) {
                // might want to
                if (response.body() != null && response.isSuccessful() && response.body().getErrorMsg().equals("")) {
                    Toast.makeText(getBaseContext(),
                            "Account has been deleted",
                            Toast.LENGTH_LONG).show();
                    onAccountDeleteSuccess();
                }
                else {
                    Toast.makeText(getBaseContext(),
                            "ERROR: " + response.body().getErrorMsg(),
                            Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<DeleteUser> call, @NonNull Throwable t) {
                Toast.makeText(getBaseContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onAccountDeleteSuccess() {
        // on delete delete cookies
        ((ApplicationStore) this.getApplication()).setFriends(new ArrayList<>());
        ((ApplicationStore) this.getApplication()).setMessages(new ArrayList<>());
        ((ApplicationStore) this.getApplication()).setUsername("");
        ((ApplicationStore) this.getApplication()).setDateCreated("");
        ((ApplicationStore) this.getApplication()).setNumberOfMessages(0);
        ((ApplicationStore) this.getApplication()).setNumberOfFriends(0);

        _deleteAccountButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void onSignOutClick() {
        // on sign out delete cookies
        ((ApplicationStore) this.getApplication()).setFriends(new ArrayList<>());
        ((ApplicationStore) this.getApplication()).setMessages(new ArrayList<>());
        ((ApplicationStore) this.getApplication()).setUsername("");
        ((ApplicationStore) this.getApplication()).setDateCreated("");
        ((ApplicationStore) this.getApplication()).setNumberOfMessages(0);
        ((ApplicationStore) this.getApplication()).setNumberOfFriends(0);

        setResult(RESULT_OK, null);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
