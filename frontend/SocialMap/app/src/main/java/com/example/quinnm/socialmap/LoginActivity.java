package com.example.quinnm.socialmap;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quinnm.socialmap.api.model.User;
import com.example.quinnm.socialmap.api.service.UserClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


// TODO: DISABLE BACKWARD BUTTON FROM GOING BACK TO LOGIN PAGE AFTER LOGGED IN

/**
 * This is the Log-in page.
 * Requests username and password.
 * Answers to a Login button and a Create Account button.
 * Redirects user to MainActivity on success
 *
 * @author Keir Armstrong
 * @since May 13, 2018
 *
 * REFERENCES:
 *  Kam Low - Basic Layout
 *      https://sourcey.com/beautiful-android-login-and-signup-screens-with-material-design/
 *  Future Studio - Retrofit Tutorial
 *      https://www.youtube.com/watch?v=j7lRiTJ_-cI
 */
public class LoginActivity extends AppCompatActivity {
    // for debugging purposes
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    // button references
    Button _loginButton, _signupButton;
    EditText _usernameText, _passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // obtain input references for this activity
        _usernameText = findViewById(R.id.input_username);
        _passwordText = findViewById(R.id.input_password);
        _loginButton = findViewById(R.id.btn_login);
        _signupButton = findViewById(R.id.btn_signup);

        // perform action on Login button click
        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // call the login method
            public void onClick(View v) {
                login();
            }
        });


        // perform action on Signup button click
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect the user to the Signup Activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    private void login() {
        // for debugging purposes
        Log.d(TAG, "Login");

//        // REMOVE THIS IN PRODUCTION
//        if (checkSuperUser()) {
//            _loginButton.setEnabled(false);
//            onLoginSuccess();
//            return;
//        }

        // perform basic validations on user inputs
        if (!validate()) {
            onLoginFail();
            return;
        }

        // disable the log in button to prevent multiple login requests
        _loginButton.setEnabled(false);

        // prepare Http request payload object
        User user = new User(
                _usernameText.getText().toString(), // username
                _passwordText.getText().toString()  // password
        );

        // display progress bar
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        // Begin Retrofit builder
        // Server connection
        // 10.0.2.2 here is replacing the "localhost" or "127.0.0.1" address of the machine hosting the emulator.
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        // create Http request object
        UserClient client = retrofit.create(UserClient.class);
        // create Http queue object
        Call<User> call = client.loginAccount(user);

        // make an asynchronous call to the server
        call.enqueue(new Callback<User>() {
            // if hear back from the server
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                // check if request performed
                if (response.body() != null && response.isSuccessful() && response.body().getErrorMsg().equals("")) {
                    // alert the user on success
                    Toast.makeText(getBaseContext(),
                            "Login successful",
                            Toast.LENGTH_LONG).show();
                    // close the progress bar
                    progressDialog.dismiss();
                    // go to the next step
                    loadUserInfo(response);
                }
                else {
                    // alert the user on failure
                    Toast.makeText(getBaseContext(),
                            "ERROR: " + response.body().getErrorMsg(),
                            Toast.LENGTH_LONG).show();
                    // close the progress bar
                    progressDialog.dismiss();
                    // re-enable the log in button
                    _loginButton.setEnabled(true);
                }

            }

            // if the request was not received
            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                // alert the user about the error
                Toast.makeText(getBaseContext(), t.toString(), Toast.LENGTH_LONG).show();
                // close the progress bar
                progressDialog.dismiss();
                // re-enable the login button
                _loginButton.setEnabled(true);
            }
        });
    }

    public void loadUserInfo(Response<User> userResponse) {
        // store the user's info in application cache
        // store the username
        ((ApplicationStore) this.getApplication()).setUsername(userResponse.body().getUsername());
        // store the user id
        ((ApplicationStore) this.getApplication()).setUserId(userResponse.body().getUserId());
        // store the date of creation
        ((ApplicationStore) this.getApplication()).setDateCreated(userResponse.body().getDateCreated());

        // go to the next step
        onLoginSuccess();
    }

    public void onLoginSuccess() {
        // re-enable the login button
        _loginButton.setEnabled(true);
        // prepare for main activity, the main UI
        Intent intent = new Intent(this, MainActivity.class);
        // start the main activity, the main UI
        startActivity(intent);
        // prevent coming back to this activity
        finish();
    }

    public void onLoginFail() {
        // re-enable the log in button
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        // set up return value
        boolean valid = true;

        // get username and password from text fields
        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        // if username is empty or not within range, return account as invalid
        if (username.isEmpty() || username.length() < 3 || username.length() > 15) {
            _usernameText.setError("between 3 and 15 characters");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        // if password is empty or not within range, return account as invalid
        if (password.isEmpty() || password.length() < 4 || password.length() > 20) {
            _passwordText.setError("between 4 and 20 characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

//    private boolean checkSuperUser() {
//        boolean valid = true;
//
//        if (!"root".equals(_usernameText.getText().toString())) {
//            valid = false;
//        }
//
//        if (!"root".equals(_passwordText.getText().toString())) {
//            valid = false;
//        }
//
//        return valid;
//    }
}
