package com.example.kickunity.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kickunity.R;

public class ProfileEditActivity extends AppCompatActivity {

    private ImageButton backButton;
    private TextView TextprofileName, Textemail;
    private Button nicknameChange, passwordChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile); // XML 레이아웃과 연결

        backButton = findViewById(R.id.backButton);
        nicknameChange = findViewById(R.id.nickname_change_button);
        passwordChange = findViewById(R.id.password_change_button);
        TextprofileName = findViewById(R.id.TextprofileName); // 닉네임 TextView
        Textemail = findViewById(R.id.Textemail); // 이메일 TextView

        // Intent로 전달된 데이터를 받아오기
        Intent intent = getIntent();
        String userEmail = intent.getStringExtra("userEmail");
        String userName = intent.getStringExtra("userName");

        // TextView에 값 설정
        if (userEmail != null) {
            Textemail.setText(userEmail); // 이메일 설정
        }
        if (userName != null) {
            TextprofileName.setText(userName); // 닉네임 설정
        }

        // Back button click listener
        backButton.setOnClickListener(view -> {
            finish(); // 현재 Activity 종료
        });

        setupNicknameChangeButton();
        setupPasswordChangeButton();
    }

    private void setupNicknameChangeButton() {
        nicknameChange.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileEditActivity.this, NicknameModifyActivity.class);
            startActivity(intent);
        });
    }

    private void setupPasswordChangeButton() {
        passwordChange.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileEditActivity.this, PasswordModifyActivity.class);
            startActivity(intent);
        });
    }
}
