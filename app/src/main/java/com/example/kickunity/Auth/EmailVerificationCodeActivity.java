package com.example.kickunity.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kickunity.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

public class EmailVerificationCodeActivity extends AppCompatActivity {

    private EditText code1, code2, code3, code4, code5, code6;
    private Button authCodeCheckedButton, resendAuthCodeButton;
    private TextView timerTextView;
    private Handler handler = new Handler();
    private int timerCount = 180;  // 3분 (180초)
    private String email;
    private ImageButton backButton;

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
        timerTextView = findViewById(R.id.timerTextView);
        backButton = findViewById(R.id.backButton);  // backButton 초기화

        // 이메일을 이전 화면에서 받아옴
        email = getIntent().getStringExtra("email");

        // 인증번호 확인 버튼 클릭 시
        authCodeCheckedButton.setOnClickListener(v -> {
            String enteredCode = getEnteredCode();
            if (enteredCode.length() == 6) {
                verifyCode(email, enteredCode);
            } else {
                Toast.makeText(EmailVerificationCodeActivity.this, "6자리 인증번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // 인증번호 재전송 버튼 클릭 시
        resendAuthCodeButton.setOnClickListener(v -> resendVerificationCode(email));

        // 타이머 시작
        startResendTimer();

        // 뒤로 가기 버튼 클릭 시 이전 화면으로 돌아가기
        backButton.setOnClickListener(v -> finish());

        // 인증번호 입력 필드 자동 포커스 이동
        setCodeFieldFocus();
    }

    // 타이머 시작 메서드
    private void startResendTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timerCount--;
                int minutes = timerCount / 60;
                int seconds = timerCount % 60;
                String time = String.format("%02d:%02d", minutes, seconds);
                timerTextView.setText(time);

                if (timerCount > 0) {
                    handler.postDelayed(this, 1000);  // 1초 후 다시 실행
                } else {
                    resendAuthCodeButton.setEnabled(true);  // 타이머 종료 후 재전송 버튼 활성화
                }
            }
        }, 1000); // 1초마다 갱신
    }

    // 인증 코드를 한 문자열로 합치는 메서드
    private String getEnteredCode() {
        String authNum = code1.getText().toString().trim() +
                code2.getText().toString().trim() +
                code3.getText().toString().trim() +
                code4.getText().toString().trim() +
                code5.getText().toString().trim() +
                code6.getText().toString().trim();

        if (authNum.length() == 6) {
            System.out.println("Entered Code: " + authNum);
            return authNum; // 6자리 인증번호만 반환
        } else {
            System.out.println("Invalid Code: " + authNum);
            return ""; // 인증번호가 6자리가 아니면 빈 문자열 반환
        }
    }

    // 인증번호 검증 메서드 (서버와 연결)
    private void verifyCode(String email, String enteredCode) {
        if (enteredCode.isEmpty()) {
            Toast.makeText(EmailVerificationCodeActivity.this, "인증번호를 정확히 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthApiService authApiService = RetrofitClient.getAuthApiService();
        EmailCheckRequest emailCheckRequest = new EmailCheckRequest(email, enteredCode);
        Call<EmailCheckResponse> call = authApiService.verifyCode(emailCheckRequest);

        call.enqueue(new Callback<EmailCheckResponse>() {
            @Override
            public void onResponse(Call<EmailCheckResponse> call, Response<EmailCheckResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EmailCheckResponse emailResponse = response.body();
                    if (emailResponse.isSuccess()) {
                        Toast.makeText(EmailVerificationCodeActivity.this, emailResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EmailVerificationCodeActivity.this, SignUpActivity.class);
                        intent.putExtra("email", email); // 이메일을 다음 화면으로 전달
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(EmailVerificationCodeActivity.this, emailResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorMessage = response.errorBody() != null ? response.errorBody().string() : "알 수 없는 오류 발생";
                        Log.e("API Error", "Error Response: " + errorMessage);
                    } catch (IOException e) {
                        Log.e("API Error", "Error parsing response body", e);
                    }
                    Toast.makeText(EmailVerificationCodeActivity.this, "인증번호가 일치하지 않습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EmailCheckResponse> call, Throwable t) {
                Log.e("Network Error", "Error: " + t.getMessage());
                Toast.makeText(EmailVerificationCodeActivity.this, "서버 오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 인증번호 재전송 메서드
    private void resendVerificationCode(String email) {
        AuthApiService authApiService = RetrofitClient.getAuthApiService();
        EmailRequest emailRequest = new EmailRequest(email);
        Call<Void> call = authApiService.sendVerificationEmail(emailRequest);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EmailVerificationCodeActivity.this, "인증번호가 다시 전송되었습니다.", Toast.LENGTH_SHORT).show();
                    timerCount = 180;  // 타이머 초기화 (3분)
                    resendAuthCodeButton.setEnabled(false);  // 재전송 버튼 비활성화
                    startResendTimer();
                } else {
                    Toast.makeText(EmailVerificationCodeActivity.this, "인증번호 전송에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EmailVerificationCodeActivity.this, "서버 오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 인증번호 입력 필드 자동 포커스 이동
    private void setCodeFieldFocus() {
        code1.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && code1.getText().length() == 1) {
                code2.requestFocus();
            }
        });

        code2.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && code2.getText().length() == 1) {
                code3.requestFocus();
            }
        });

        code3.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && code3.getText().length() == 1) {
                code4.requestFocus();
            }
        });

        code4.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && code4.getText().length() == 1) {
                code5.requestFocus();
            }
        });

        code5.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && code5.getText().length() == 1) {
                code6.requestFocus();
            }
        });

        code6.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && code6.getText().length() == 1) {
                authCodeCheckedButton.performClick();
            }
        });
    }
}