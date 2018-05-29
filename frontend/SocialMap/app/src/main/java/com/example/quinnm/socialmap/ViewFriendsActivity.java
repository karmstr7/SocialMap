package com.example.quinnm.socialmap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ViewFriendsActivity extends AppCompatActivity {
    private Button _mainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friends);
        _mainButton = findViewById(R.id.btn_friends_to_main);

        _mainButton.setOnClickListener(
                (View v) -> finish()
        );
    }
}
