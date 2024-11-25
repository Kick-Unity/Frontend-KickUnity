package com.example.kickunity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonJoin, buttonFindPassword;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initializeViews();
        setupLoginButton();
        setupJoinButton();
        setupFindPassword();
    }

    private void initializeViews() {
        editTextEmail = findViewById(R.id.emailInput);
        editTextPassword = findViewById(R.id.passwordInput);
        buttonLogin = findViewById(R.id.loginButton);
        buttonJoin = findViewById(R.id.join);
        buttonFindPassword = findViewById(R.id.find_password);
    }

    private void setupLoginButton() {
        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(email, password);
        });
    }

    private void setupJoinButton() {
        buttonJoin.setOnClickListener(v -> {
            // email_verification 이동
            Intent intent = new Intent(MainActivity.this, EmailVerificationActivity.class);
            startActivity(intent);
        });
    }

    private void setupFindPassword() {
        buttonFindPassword.setOnClickListener(v -> {
            // FindPasswordActivity 이동
            Intent intent = new Intent(MainActivity.this, FindPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser(String email, String password) {
        // Retrofit 설정
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://15.165.133.129:8080/")  // 기본 URL ("/api/login" 경로는 @POST 어노테이션에 포함됨)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        LoginRequest loginRequest = new LoginRequest(email, password);

        // 로그인 요청시 프로그레스 다이얼로그 표시
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("로그인 중...");
        progressDialog.show();

        Call<LoginResponse> call = apiService.login(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                // 프로그레스 다이얼로그 닫기
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    String accessToken = loginResponse.getAccessToken();
                    String refreshToken = loginResponse.getRefreshToken();

                    // 토큰 저장
                    saveTokens(accessToken, refreshToken);

                    // 이메일도 SharedPreferences에 저장
                    saveUserEmail(email);

                    Toast.makeText(MainActivity.this, "로그인 성공!", Toast.LENGTH_SHORT).show();
                    redirectToHome();
                } else {
                    Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                Log.e(TAG, "Network error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTokens(String accessToken, String refreshToken) {
        getSharedPreferences("auth", MODE_PRIVATE)
                .edit()
                .putString("accessToken", accessToken)
                .putString("refreshToken", refreshToken)
                .apply();
    }

    private void saveUserEmail(String email) {
        getSharedPreferences("auth", MODE_PRIVATE)
                .edit()
                .putString("userEmail", email)  // 이메일 저장
                .apply();
    }


    private void redirectToHome() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}