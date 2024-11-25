package com.example.kickunity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailVerificationActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button verifyButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_verification);

        // 뷰 초기화
        emailEditText = findViewById(R.id.editTextEmail);
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
                    // 이메일이 유효한 경우 서버에 이메일 인증 요청
                    sendVerificationEmail(email);
                }
            }
        });
    }

    // 인증번호 전송 메서드
    private void sendVerificationEmail(String email) {
        // Retrofit 인스턴스를 가져옵니다.
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        // 이메일 인증번호 요청시 프로그레스 다이얼로그 표시
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("인증번호 요청중...");
        progressDialog.show();

        // 서버로 이메일 전송 요청
        EmailRequest emailRequest = new EmailRequest(email);
        Call<Void> call = apiService.sendVerificationEmail(emailRequest);

        // 서버 요청을 비동기 방식으로 실행
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EmailVerificationActivity.this, "인증번호가 이메일로 전송되었습니다.", Toast.LENGTH_SHORT).show();
                    // 인증번호 입력 화면으로 이동
                    Intent intent = new Intent(EmailVerificationActivity.this, EmailVerificationCodeActivity.class);
                    intent.putExtra("email", email); // 이메일을 다음 화면으로 전달
                    startActivity(intent);
                } else {
                    // 서버 응답이 실패했을 때
                    Toast.makeText(EmailVerificationActivity.this, "인증번호 전송에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // 네트워크 오류 처리
                Toast.makeText(EmailVerificationActivity.this, "서버 오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
