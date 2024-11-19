package com.example.kickunity;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class NicknameChangeActivity extends AppCompatActivity {
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname_change); // XML 레이아웃과 연결

        backButton = findViewById(R.id.backButton);

        // Back button click listener
        backButton.setOnClickListener(view -> {
            finish(); // 현재 Activity 종료
        });
    }
}
