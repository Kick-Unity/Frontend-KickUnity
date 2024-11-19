package com.example.kickunity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PasswordChangeActivity extends AppCompatActivity {
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change); // XML 레이아웃과 연결

        backButton = findViewById(R.id.backButton);

        // Back button click listener
        backButton.setOnClickListener(view -> {
            finish(); // 현재 Activity 종료
        });

        TextView passwordProtectionMessage = findViewById(R.id.password_protection_message_text);
        // 전체 텍스트를 생성
        String fullText = "새로운 사이트에만 사용하는 비밀번호를 만드세요.";
        // SpannableString을 사용하여 텍스트 스타일 적용
        SpannableString spannableString = new SpannableString(fullText);

        // 첫 19글자에 파란색과 밑줄 추가
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#007BFF")), 0, 19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new UnderlineSpan(), 0, 19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // TextView에 적용
        passwordProtectionMessage.setText(spannableString);



        TextView safePasswordMessage = findViewById(R.id.safe_password_message_text);
        // 전체 텍스트를 생성
        String fullText2 = "이전에 사용한 적 없는 비밀번호가 안전합니다.";
        // 특정 부분만 색상을 변경
        SpannableString spannableString2 = new SpannableString(fullText2);
        spannableString2.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6347")), 0, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // TextView에 적용
        safePasswordMessage.setText(spannableString2);




    }
}
