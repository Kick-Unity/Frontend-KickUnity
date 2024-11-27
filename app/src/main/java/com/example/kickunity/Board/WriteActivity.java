package com.example.kickunity.Board;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kickunity.R;
import com.example.kickunity.Auth.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WriteActivity extends AppCompatActivity {

    private static final String TAG = "WriteActivity";

    private ImageButton backButton;
    private EditText titleEditText;
    private EditText contentEditText;
    private Spinner categorySpinner;

    private BoardApiService boardApiService;

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
        boardApiService = RetrofitClient.getBoardApiService();

        // 카테고리 스피너 설정
        setupCategorySpinner();

        // 기본 카테고리 설정
        setDefaultCategory();

        // 뒤로 가기 버튼 리스너
        backButton.setOnClickListener(view -> finish());

        // 제출 버튼 리스너
        submitButton.setOnClickListener(v -> handleSubmit());
    }

    // SharedPreferences에서 데이터 읽기 유틸리티 메서드
    private String getFromSharedPreferences(String key) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("auth", MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    // 카테고리 스피너 설정
    private void setupCategorySpinner() {
        String[] categories = {"ALL", "SOCCER", "BASKETBALL", "BASEBALL", "ETC"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    // 기본 카테고리 설정
    private void setDefaultCategory() {
        String defaultCategory = getIntent().getStringExtra("defaultCategory");
        if (defaultCategory != null) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) categorySpinner.getAdapter();
            int position = adapter.getPosition(defaultCategory);
            if (position >= 0) {
                categorySpinner.setSelection(position); // 기본 카테고리 선택
            }
        }
    }

    // 게시글 제출 처리
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

        // 요청 객체 생성
        AddBoardRequest request = new AddBoardRequest(title, content, category);

        // SharedPreferences에서 토큰 읽기
        String accessToken = getFromSharedPreferences("accessToken");

        if (accessToken == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String authorizationHeader = "Bearer " + accessToken;

        // 서버로 게시글 전송
        boardApiService.addBoard(authorizationHeader, request).enqueue(new Callback<BoardDetailResponse>() {
            @Override
            public void onResponse(Call<BoardDetailResponse> call, Response<BoardDetailResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(WriteActivity.this, "게시글이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();

                    // 게시글 ID 가져오기
                    long postId = response.body().getId();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("postId", postId); // 게시글 ID 전달
                    setResult(RESULT_OK, resultIntent);
                    finish(); // 성공 후 액티비티 종료
                } else {
                    handleError(response);
                }
            }

            @Override
            public void onFailure(Call<BoardDetailResponse> call, Throwable t) {
                Log.e(TAG, "서버와의 연결에 실패했습니다.", t);
                Toast.makeText(WriteActivity.this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // 에러 처리
    private void handleError(Response<?> response) {
        String errorMessage;
        try {
            errorMessage = response.errorBody() != null ? response.errorBody().string() : "응답 본문 없음";
        } catch (Exception e) {
            errorMessage = "에러 본문 읽기 실패: " + e.getMessage();
        }
        Log.e(TAG, "에러 코드: " + response.code() + ", 메시지: " + errorMessage);
        Toast.makeText(this, "게시글 등록에 실패했습니다.", Toast.LENGTH_SHORT).show();
    }
}
