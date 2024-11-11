package com.example.kickunity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EmailVerificationActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button verifyButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_verification);

        // 뷰 초기화
        emailEditText = findViewById(R.id.sign_email);
        verifyButton = findViewById(R.id.buttonSignUp);
        backButton = findViewById(R.id.backButton);

        // 뒤로 가기 버튼 클릭 시 이전 화면으로 돌아가기
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 현재 액티비티 종료
            }
        });

        // 이메일 인증 버튼 클릭 시
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();

                // 이메일 입력 검증
                if (email.isEmpty()) {
                    Toast.makeText(EmailVerificationActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(EmailVerificationActivity.this, "올바른 이메일 주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    // 이메일이 유효한 경우 인증번호 전송 및 다음 액티비티로 이동
                    sendVerificationCode(email);
                    Intent intent = new Intent(EmailVerificationActivity.this, EmailVerificationCodeActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    // 인증번호 전송 메서드 (추후 실제 기능 구현)
    private void sendVerificationCode(String email) {
        // TODO: 서버에 이메일 인증 요청을 보내는 코드 작성
        Toast.makeText(this, "인증번호가 이메일로 전송되었습니다: " + email, Toast.LENGTH_SHORT).show();
    }
}
