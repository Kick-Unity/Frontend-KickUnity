package com.example.kickunity;

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

public class SignIn2Activity extends AppCompatActivity {
    private static final String TAG = "SignIn2Activity";
    private ApiService apiService;

    private EditText editTextEmail, editTextPassword, editTextName, editTextBirth;
    private Button buttonSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in2);  // XML 레이아웃 파일

        initializeViews();
        setupRetrofit();
        setupSignUpButton();
    }

    private void initializeViews() {
        // EditText와 Button 초기화
        editTextEmail = findViewById(R.id.sign_email);
        editTextPassword = findViewById(R.id.sign_password);
        editTextName = findViewById(R.id.sign_name);
        editTextBirth = findViewById(R.id.sign_birth);
        buttonSignUp = findViewById(R.id.buttonSignUp);
    }

    private void setupRetrofit() {
        // Retrofit 초기화
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.BASE_URL)  // ApiService에서 BASE_URL을 가져옵니다
                .addConverterFactory(GsonConverterFactory.create())  // GsonConverterFactory로 JSON 처리
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    private void setupSignUpButton() {
        // 버튼 클릭 시 회원가입 처리
        buttonSignUp.setOnClickListener(view -> {
            if (validateInputs()) {
                signUpUser();  // 입력값 검증 후 회원가입 요청
            }
        });
    }

    private boolean validateInputs() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String birth = editTextBirth.getText().toString().trim();

        // 입력값이 비어있는지 체크
        if (email.isEmpty()) {
            showToast("이메일을 입력해주세요.");
            return false;
        }
        if (password.isEmpty()) {
            showToast("비밀번호를 입력해주세요.");
            return false;
        }
        if (name.isEmpty()) {
            showToast("이름을 입력해주세요.");
            return false;
        }
        if (birth.isEmpty()) {
            showToast("생일을 입력해주세요.");
            return false;
        }

        return true;
    }

    private void signUpUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String birth = editTextBirth.getText().toString().trim();

        // JoinRequest 객체 생성
        JoinRequest joinRequest = new JoinRequest(email, password, name, birth);

        // 서버에 회원가입 요청
        apiService.signUpUser(joinRequest).enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.isSuccessful() && response.body() != null) {
                    long memberId = response.body();  // 서버에서 반환한 회원 ID
                    showToast("회원가입 성공! 회원 ID: " + memberId);
                    finish();  // 회원가입 성공 시, 액티비티 종료
                } else {
                    String errorMessage = "회원가입 실패";
                    try {
                        errorMessage = response.errorBody().string(); // 서버의 에러 메시지 가져오기
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body", e);
                    }
                    showToast(errorMessage);
                    Log.e(TAG, "회원가입 실패: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                // 네트워크 오류 처리
                Log.e(TAG, "네트워크 오류: " + t.getMessage());
                showToast("네트워크 오류: " + t.getMessage());
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(SignIn2Activity.this, message, Toast.LENGTH_SHORT).show();
    }
}
