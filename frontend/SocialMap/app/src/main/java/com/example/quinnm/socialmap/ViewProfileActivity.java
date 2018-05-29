package com.example.quinnm.socialmap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ViewProfileActivity extends AppCompatActivity {
    private Button _mainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        _mainButton = findViewById(R.id.btn_profile_to_main);

        _mainButton.setOnClickListener(
                (View v) -> finish()
        );
    }
}
