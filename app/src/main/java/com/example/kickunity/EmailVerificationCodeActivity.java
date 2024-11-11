package com.example.kickunity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EmailVerificationCodeActivity extends AppCompatActivity {

    private EditText code1, code2, code3, code4, code5, code6;
    private Button authCodeCheckedButton, resendAuthCodeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_verification_code);

        // 뷰 초기화
        code1 = findViewById(R.id.sign_certNum_1);
        code2 = findViewById(R.id.sign_certNum_2);
        code3 = findViewById(R.id.sign_certNum_3);
        code4 = findViewById(R.id.sign_certNum_4);
        code5 = findViewById(R.id.sign_certNum_5);
        code6 = findViewById(R.id.sign_certNum_6);

        authCodeCheckedButton = findViewById(R.id.authCodeChecked_Button);
        resendAuthCodeButton = findViewById(R.id.resendAuthCode_Button);

        // 인증번호 확인 버튼 클릭 시
        authCodeCheckedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredCode = getEnteredCode();

                // 코드 검증 로직
                if (verifyCode(enteredCode)) {
                    // 코드가 일치하면 다음 액티비티로 이동
                    Intent intent = new Intent(EmailVerificationCodeActivity.this, SignUpActivity.class);
                    startActivity(intent);
                    finish(); // 현재 액티비티 종료
                } else {
                    Toast.makeText(EmailVerificationCodeActivity.this, "인증번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 다시 보내기 버튼 클릭 시
        resendAuthCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode();
            }
        });
    }

    // 인증 코드를 한 문자열로 합치는 메서드
    private String getEnteredCode() {
        return code1.getText().toString().trim() +
                code2.getText().toString().trim() +
                code3.getText().toString().trim() +
                code4.getText().toString().trim() +
                code5.getText().toString().trim() +
                code6.getText().toString().trim();
    }

    // 인증 코드 검증 메서드 (여기서는 예시로 "123456"이 맞는 코드라 가정)
    private boolean verifyCode(String enteredCode) {
        String correctCode = "123456"; // 실제 프로젝트에서는 서버에서 받은 코드를 사용
        return enteredCode.equals(correctCode);
    }

    // 인증 코드를 다시 전송하는 메서드
    private void resendVerificationCode() {
        // TODO: 서버와 통신하여 인증 코드를 다시 전송하는 로직을 작성
        Toast.makeText(this, "인증번호가 다시 전송되었습니다.", Toast.LENGTH_SHORT).show();
    }
}
