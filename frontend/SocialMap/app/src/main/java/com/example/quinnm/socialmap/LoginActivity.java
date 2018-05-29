package com.example.quinnm.socialmap;

import android.app.ProgressDialog;
import android.os.Bundle;
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
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    Button _loginButton, _signupButton;
    EditText _usernameText, _passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _usernameText = findViewById(R.id.input_username);
        _passwordText = findViewById(R.id.input_password);
        _loginButton = findViewById(R.id.btn_login);
        _signupButton = findViewById(R.id.btn_signup);

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    private void login() {
        Log.d(TAG, "Login");

        // REMOVE THIS IN PRODUCTION
        if (checkSuperUser()) {
            _loginButton.setEnabled(false);
            onLoginSuccess();
            return;
        }

        if (!validate()) {
            onLoginFail();
            return;
        }

        _loginButton.setEnabled(false);

        User user = new User(
                _usernameText.getText().toString(),
                _passwordText.getText().toString()
        );

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        // Server connection
        // 10.0.2.2 here is replacing the "localhost" or "127.0.0.1" address of the machine hosting the emulator.
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/socialmap/api/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        UserClient client = retrofit.create(UserClient.class);
        Call<User> call = client.loginAccount(user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Toast.makeText(getBaseContext(), "Success" + response.body(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                onLoginSuccess();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getBaseContext(), t.toString(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                _loginButton.setEnabled(true);
            }
        });
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        LoginActivity.this.startActivity(intent);
    }

    public void onLoginFail() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty() || username.length() < 3 || username.length() > 15) {
            _usernameText.setError("between 3 and 15 characters");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 20) {
            _passwordText.setError("between 4 and 20 characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private boolean checkSuperUser() {
        boolean valid = true;

        if (!"root".equals(_usernameText.getText().toString())) {
            valid = false;
        }

        if (!"root".equals(_passwordText.getText().toString())) {
            valid = false;
        }

        return valid;
    }
}
