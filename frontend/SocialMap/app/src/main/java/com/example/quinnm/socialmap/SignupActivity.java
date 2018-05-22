package com.example.quinnm.socialmap;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.example.quinnm.socialmap.api.model.User;
import com.example.quinnm.socialmap.api.service.UserClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    EditText _username, _password;
    Button _signupButton;
    TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        _username = findViewById(R.id.input_username);
        _password = findViewById(R.id.input_password);
        _signupButton = findViewById(R.id.btn_signup);
        _loginLink = findViewById(R.id.link_login);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFail();
            return;
        }

        _signupButton.setEnabled(false);

        User user = new User(
                _username.getText().toString(),
                _password.getText().toString()
        );

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        // Server connection
        // 10.0.2.2 here is replacing the "localhost" or "127.0.0.1" address of the machine hosting the emulator.
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/socialmap/api/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        UserClient client = retrofit.create(UserClient.class);
        Call<User> call = client.createAccount(user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Toast.makeText(getBaseContext(), "Success" + response.body(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                onSignupSuccess();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getBaseContext(), t.toString(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                _signupButton.setEnabled(true);
            }
        });
    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        SignupActivity.this.startActivity(intent);
    }

    public void onSignupFail() {
        Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _username.getText().toString();
        String password = _password.getText().toString();

        if (username.isEmpty() || username.length() < 3 || username.length() > 15) {
            _username.setError("between 3 and 15 characters");
            valid = false;
        } else {
            _username.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 20) {
            _password.setError("between 4 and 20 characters");
            valid = false;
        } else {
            _password.setError(null);
        }

        return valid;
    }
}