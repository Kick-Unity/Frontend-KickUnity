package com.example.kickunity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileEditActivity extends AppCompatActivity {

    private ImageButton backButton;
    private Button nicknameChange, passwordChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit); // XML 레이아웃과 연결

        backButton = findViewById(R.id.backButton);
        nicknameChange = findViewById(R.id.nickname_change_button);
        passwordChange = findViewById(R.id.password_change_button);

        // Back button click listener
        backButton.setOnClickListener(view -> {
            finish(); // 현재 Activity 종료
        });

        setupNicknameChangeButton();
        setupPasswordChangeButton();

    }

    private void setupNicknameChangeButton() {
        nicknameChange.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileEditActivity.this, NicknameChangeActivity.class);
            startActivity(intent);
        });
    }

    private void setupPasswordChangeButton() {
        passwordChange.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileEditActivity.this, PasswordChangeActivity.class);
            startActivity(intent);
        });
    }

}
