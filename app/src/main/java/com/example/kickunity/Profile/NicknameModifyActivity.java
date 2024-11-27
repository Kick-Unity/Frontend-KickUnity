package com.example.kickunity.Profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kickunity.Auth.AuthApiService;
import com.example.kickunity.Auth.CheckResponse;
import com.example.kickunity.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NicknameModifyActivity extends AppCompatActivity {

    private ImageButton backButton;
    private Button completionButton;
    private EditText nicknameInput;

    private AuthApiService authApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname_modify); // XML 레이아웃 연결

        // 뷰 초기화
        backButton = findViewById(R.id.backButton);
        completionButton = findViewById(R.id.Completion_button);
        nicknameInput = findViewById(R.id.nicknameInput);

        // Retrofit 초기화
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://15.165.133.129:8080/") // 실제 서버의 base URL로 수정
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        authApiService = retrofit.create(AuthApiService.class);

        // 뒤로가기 버튼 클릭 리스너
        backButton.setOnClickListener(v -> onBackPressed());

        String accessToken = getAccessTokenFromSharedPreferences();  // 액세스 토큰을 가져옵니다.

        // 완료 버튼 클릭 리스너
        completionButton.setOnClickListener(v -> {
            String newNickname = nicknameInput.getText().toString();
            if (!newNickname.isEmpty()) {
                changeNickname(newNickname, accessToken);  // 토큰 전달
            } else {
                Toast.makeText(NicknameModifyActivity.this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeNickname(String newNickname, String accessToken) {
        // Authorization header 준비
        String authorizationHeader = "Bearer " + accessToken;  // 실제 인증 토큰을 넣어야 합니다.

        // ChangeNameRequest 객체 생성
        ChangeNameRequest request = new ChangeNameRequest(newNickname);

        // Retrofit을 통해 서버에 API 요청
        authApiService.changeNickname(authorizationHeader, request).enqueue(new Callback<CheckResponse>() {
            @Override
            public void onResponse(Call<CheckResponse> call, Response<CheckResponse> response) {
                // 응답 로그 출력
                Log.d("API Response", "Code: " + response.code() + ", Message: " + response.message());

                if (response.isSuccessful() && response.body() != null) {
                    CheckResponse checkResponse = response.body();
                    if (checkResponse.isSuccess()) {
                        // 닉네임 변경 성공
                        Toast.makeText(NicknameModifyActivity.this, "닉네임이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        finish(); // 액티비티 종료
                    } else {
                        // 실패 시 메시지 출력
                        String message = checkResponse.getMessage();
                        if ("이미 사용 중인 닉네임입니다.".equals(message)) {
                            Toast.makeText(NicknameModifyActivity.this, "이미 사용 중인 닉네임입니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(NicknameModifyActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // 서버 오류
                    Log.e("API Error", "Error response code: " + response.code());
                    Toast.makeText(NicknameModifyActivity.this, "서버 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CheckResponse> call, Throwable t) {
                // 네트워크 오류 처리
                Log.e("API Failure", "Network failure: " + t.getMessage());
                Toast.makeText(NicknameModifyActivity.this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String getAccessTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        String token = sharedPreferences.getString("accessToken", null);
        if (token == null) {
            Toast.makeText(this, "토큰이 없습니다. 다시 로그인 해주세요.", Toast.LENGTH_SHORT).show();
        }
        return token;  // 토큰이 없으면 null 반환
    }
}
