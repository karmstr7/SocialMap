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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        _numberOfMessages = ((ApplicationStore) this.getApplication()).getNumberOfMessages();
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
        _deleteAccountButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        ViewProfileActivity.this.startActivity(intent);
        finish();
    }

    private void onSignOutClick() {
        setResult(RESULT_OK, null);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        ViewProfileActivity.this.startActivity(intent);
        finish();
    }
}
