package com.example.kickunity;

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

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    private ApiService apiService;
    private EditText editTextEmail, editTextPassword, editPasswordCheck, editTextName, editTextBirth;
    private Button buttonSignUp;
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join);  // XML 레이아웃 파일

        initializeViews();
        setupRetrofit();
        setupSignUpButton();

        // 이메일을 이전 화면에서 받아옴
        email = getIntent().getStringExtra("email");

        // 이메일을 입력 필드에 자동으로 채워넣기
        if (email != null && !email.isEmpty()) {
            editTextEmail.setText(email);
        }
    }


    private void initializeViews() {
        // EditText와 Button 초기화
        editTextEmail = findViewById(R.id.sign_email);
        editTextPassword = findViewById(R.id.sign_password);
        editPasswordCheck = findViewById(R.id.sign_passwordCheck);
        editTextName = findViewById(R.id.sign_name);
        editTextBirth = findViewById(R.id.sign_birth);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        // 이메일 필드를 수정 불가능하게 설정
        editTextEmail.setEnabled(false);  // 이메일을 수정할 수 없도록 설정
        editTextEmail.setFocusable(false);  // 이메일 필드를 포커스 할 수 없도록 설정
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
        String passwordCheck = editPasswordCheck.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String birth = editTextBirth.getText().toString().trim();

        if (email.isEmpty()) {
            showToast("이메일을 입력해주세요.");
            return false;
        }
        if (password.isEmpty()) {
            showToast("비밀번호를 입력해주세요.");
            return false;
        }
        if (passwordCheck.isEmpty()) {
            showToast("비밀번호 확인을 입력해주세요.");
            return false;
        }
        if (!password.equals(passwordCheck)) {  // 비밀번호가 일치하지 않으면
            showToast("비밀번호가 일치하지 않습니다.");  // 토스트 메시지 표시
            return false;  // 더 이상의 처리를 하지 않고 리턴
        }
        // 비밀번호가 영문, 숫자, 특수문자 포함 8~16자 사이인지 체크
        if (!isPasswordValid(password)) {
            showToast("비밀번호는 8~16자 사이로, 영문, 숫자, 특수문자를 포함해야 합니다.");
            return false;
        }
        if (name.isEmpty()) {
            showToast("닉네임을 입력해주세요.");
            return false;
        }
        if (birth.isEmpty()) {
            showToast("생일을 입력해주세요.");
            return false;
        }

        return true;
    }

    // 비밀번호 유효성 검사 (영문, 숫자, 특수문자 포함, 8~16자)
    private boolean isPasswordValid(String password) {
        // 정규식: 최소 하나의 영문, 숫자, 특수문자, 길이 8~16자
        String regex = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,16}$";
        return password.matches(regex);
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

                    // 회원가입 후, 이전 액티비티 모두 종료하고 MainActivity로 이동
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 모든 액티비티 종료
                    startActivity(intent);
                    finish();  // 현재 액티비티 종료
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
        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}