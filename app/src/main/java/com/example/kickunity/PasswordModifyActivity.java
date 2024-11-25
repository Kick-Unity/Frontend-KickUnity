package com.example.kickunity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PasswordModifyActivity extends AppCompatActivity {

    private ImageButton backButton;
    private EditText currentPasswordInput, newPasswordInput, confirmNewPasswordInput;
    private Button confirmButton;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_modify); // XML 레이아웃 연결

        // UI 요소 초기화
        backButton = findViewById(R.id.backButton);
        currentPasswordInput = findViewById(R.id.current_password_input);
        newPasswordInput = findViewById(R.id.new_password_input);
        confirmNewPasswordInput = findViewById(R.id.confirm_new_password_input);
        confirmButton = findViewById(R.id.confirm_button);

        // Retrofit 초기화
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://15.165.133.129:8080/") // 실제 서버의 base URL로 수정
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        // 뒤로가기 버튼 클릭 리스너
        backButton.setOnClickListener(view -> finish());

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

        // 비밀번호 변경 요청 처리
        confirmButton.setOnClickListener(v -> {
            String currentPasswordText = currentPasswordInput.getText().toString();
            String newPasswordText = newPasswordInput.getText().toString();
            String confirmPasswordText = confirmNewPasswordInput.getText().toString();

            // 비밀번호 확인 로직
            if (TextUtils.isEmpty(currentPasswordText)) {
                Toast.makeText(this, "현재 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(newPasswordText)) {
                Toast.makeText(this, "새 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(confirmPasswordText)) {
                Toast.makeText(this, "새 비밀번호 확인을 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else if (!newPasswordText.equals(confirmPasswordText)) {
                Toast.makeText(this, "새 비밀번호와 새 비밀번호 확인이 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            } else if (newPasswordText.equals(currentPasswordText)) {
                // 새 비밀번호가 현재 비밀번호와 같을 경우
                Toast.makeText(this, "새 비밀번호는 현재 비밀번호와 다르게 설정해야 합니다.", Toast.LENGTH_SHORT).show();
            } else {
                // 서버로 비밀번호 변경 요청
                changePassword(currentPasswordText, newPasswordText);
            }
        });
    }

    // 비밀번호 변경 서버 요청
    private void changePassword(String currentPassword, String newPassword) {
        // Authorization 헤더 준비 (예시: SharedPreferences에서 액세스 토큰을 가져옵니다.)
        String authorizationHeader = "Bearer " + getAccessTokenFromSharedPreferences(); // 실제 토큰을 SharedPreferences에서 가져오기

        // ChangePasswordRequest 객체 생성
        ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword);

        // Retrofit을 통해 서버에 비밀번호 변경 요청
        apiService.changePassword(authorizationHeader, request).enqueue(new Callback<CheckResponse>() {
            @Override
            public void onResponse(Call<CheckResponse> call, Response<CheckResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CheckResponse checkResponse = response.body();
                    if (checkResponse.isSuccess()) {
                        // 비밀번호 변경 성공
                        Toast.makeText(PasswordModifyActivity.this, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        finish(); // 액티비티 종료
                    } else {
                        // 서버에서 받은 실패 메시지 출력
                        String errorMessage = checkResponse.getMessage();

                        // 서버 메시지에 따라 다르게 처리
                        if (errorMessage != null) {
                            // "현재 비밀번호가 일치하지 않습니다"인 경우
                            if (errorMessage.contains("현재 비밀번호가 일치하지 않습니다")) {
                                Toast.makeText(PasswordModifyActivity.this, "현재 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                            }
                            // 기타 오류 메시지 처리
                            else {
                                Toast.makeText(PasswordModifyActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PasswordModifyActivity.this, "알 수 없는 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // 서버 응답이 비정상일 경우
                    Toast.makeText(PasswordModifyActivity.this, "서버 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CheckResponse> call, Throwable t) {
                // 네트워크 오류 처리
                Toast.makeText(PasswordModifyActivity.this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    // SharedPreferences에서 액세스 토큰 가져오기
    private String getAccessTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", null);  // 저장된 accessToken 반환, 없으면 null 반환
    }
}