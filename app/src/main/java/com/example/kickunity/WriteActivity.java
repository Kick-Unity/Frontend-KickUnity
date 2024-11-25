package com.example.kickunity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WriteActivity extends AppCompatActivity {

    private ImageButton backButton;
    private EditText titleEditText;
    private EditText contentEditText;
    private Spinner categorySpinner;

    private ApiService apiService; // ApiService 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        // UI 요소 초기화
        backButton = findViewById(R.id.backButton);
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        Button submitButton = findViewById(R.id.submitButton);

        // Retrofit 초기화
        apiService = RetrofitClient.getClient().create(ApiService.class);

        String[] categories = {"ALL", "SOCCER", "BASKETBALL", "BASEBALL", "ETC"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // 전달받은 기본 카테고리 값
        String defaultCategory = getIntent().getStringExtra("defaultCategory");

        // 기본 카테고리가 null이 아니면 스피너에 선택된 카테고리 설정
        if (defaultCategory != null) {
            int position = adapter.getPosition(defaultCategory);
            if (position >= 0) {
                categorySpinner.setSelection(position); // 카테고리 선택
            }
        }


        // Back 버튼 리스너
        backButton.setOnClickListener(view -> finish());

        // 제출 버튼 클릭 리스너
        submitButton.setOnClickListener(v -> handleSubmit());
    }

    // SharedPreferences에서 사용자 이메일 가져오기
    private String getUserEmailFromSharedPreferences() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("auth", MODE_PRIVATE);
        return sharedPreferences.getString("userEmail", null);  // 저장된 이메일 값 반환, 없으면 null 반환
    }

    // SharedPreferences에서 액세스 토큰 가져오기
    private String getAccessTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("auth", MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", null);  // 저장된 accessToken 반환, 없으면 null 반환
    }

    /**
     * 게시글 제출 처리 메서드
     */
    private void handleSubmit() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        // 유효성 검사
        if (title.isEmpty()) {
            Toast.makeText(this, "제목을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (content.isEmpty()) {
            Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // AddBoardRequest 객체 생성
        AddBoardRequest request = new AddBoardRequest(title, content, category);

        // SharedPreferences에서 로그인된 사용자 이메일과 액세스 토큰 읽기
        String userEmail = getUserEmailFromSharedPreferences();
        String accessToken = getAccessTokenFromSharedPreferences();  // 액세스 토큰을 가져옵니다.

        // Authorization 헤더 생성
        String authorizationHeader = "Bearer " + accessToken;

        // 서버로 게시글 전송
        apiService.addBoard(authorizationHeader, request).enqueue(new Callback<BoardDetailResponse>() {
            @Override
            public void onResponse(Call<BoardDetailResponse> call, Response<BoardDetailResponse> response) {
                if (response.isSuccessful()) {
                    // 성공적인 응답 처리
                    Toast.makeText(WriteActivity.this, "게시글이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    finish(); // 성공 후 종료
                } else {
                    // 실패한 경우, 응답 코드와 서버 메시지를 로그로 출력
                    String errorMessage = "게시글 등록에 실패했습니다. 응답 코드: " + response.code();
                    try {
                        // 서버에서 제공하는 상세한 에러 메시지가 있으면 출력
                        String responseBody = response.errorBody() != null ? response.errorBody().string() : "응답 본문 없음";
                        errorMessage += ", 서버 메시지: " + responseBody;
                    } catch (Exception e) {
                        errorMessage += ", 오류 메시지: " + e.getMessage();
                    }

                    // 에러 메시지를 로그에 출력
                    Log.e("WriteActivity", errorMessage);
                    Toast.makeText(WriteActivity.this, "게시글 등록에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BoardDetailResponse> call, Throwable t) {
                // 네트워크 오류나 기타 이유로 요청이 실패한 경우
                Log.e("WriteActivity", "서버와의 연결에 실패했습니다.", t);
                Toast.makeText(WriteActivity.this, "서버와의 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

    }


}
