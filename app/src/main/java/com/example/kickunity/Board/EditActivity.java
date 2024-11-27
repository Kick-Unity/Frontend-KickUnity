package com.example.kickunity.Board;

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

public class EditActivity extends AppCompatActivity {

    private BoardApiService boardApiService;
    private Long boardId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editboard);

        // Retrofit 초기화
        boardApiService = RetrofitClient.getBoardApiService();

        // 뒤로 가기 버튼 설정
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish()); // 액티비티 종료

        // 전달된 게시글 ID 받기
        boardId = getIntent().getLongExtra("boardId", -1L); // 기본값 -1
        Log.d("EditActivity", "Received board ID: " + boardId);

        // 게시글 상세 정보를 가져옴
        fetchBoardDetails(boardId);

        // submit 버튼 설정
        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(v -> onSubmitClicked()); // submit 클릭 시 메서드 호출
    }

    // 게시글 상세 정보 가져오는 메서드
    private void fetchBoardDetails(Long boardId) {
        String accessToken = getSharedPreferences("auth", MODE_PRIVATE).getString("accessToken", null);

        if (accessToken == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String authorizationHeader = "Bearer " + accessToken;

        boardApiService.getBoardDetail(boardId).enqueue(new Callback<BoardDetailResponse>() {
            @Override
            public void onResponse(Call<BoardDetailResponse> call, Response<BoardDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BoardDetailResponse boardDetail = response.body();
                    populateFields(boardDetail); // 텍스트 필드 및 스피너 채우기
                } else {
                    Toast.makeText(EditActivity.this, "게시글을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BoardDetailResponse> call, Throwable t) {
                Toast.makeText(EditActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 텍스트 필드 및 스피너 채우기
    private void populateFields(BoardDetailResponse boardDetail) {
        // 제목, 내용, 카테고리 입력 필드에 데이터 설정
        EditText titleEditText = findViewById(R.id.editTitle);
        EditText contentEditText = findViewById(R.id.editContent);
        Spinner categorySpinner = findViewById(R.id.categorySpinner);

        titleEditText.setText(boardDetail.getTitle());
        contentEditText.setText(boardDetail.getContent());

        // 카테고리 스피너 설정
        setupCategorySpinner(categorySpinner, boardDetail.getCategory());
    }

    // 카테고리 스피너 설정
    private void setupCategorySpinner(Spinner categorySpinner, String category) {
        String[] categories = {"ALL", "SOCCER", "BASKETBALL", "BASEBALL", "ETC"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        if (category != null) {
            int position = adapter.getPosition(category);
            if (position >= 0) {
                categorySpinner.setSelection(position); // 카테고리 선택
            }
        }
    }

    // 수정 버튼 클릭 시
    private void onSubmitClicked() {
        EditText titleEditText = findViewById(R.id.editTitle);
        EditText contentEditText = findViewById(R.id.editContent);
        Spinner categorySpinner = findViewById(R.id.categorySpinner);

        String updatedTitle = titleEditText.getText().toString().trim();
        String updatedContent = contentEditText.getText().toString().trim();
        String updatedCategory = categorySpinner.getSelectedItem().toString();

        // 수정된 데이터를 서버에 전송하는 메서드 호출
        updateBoard(boardId, updatedTitle, updatedContent, updatedCategory);
    }

    private void updateBoard(Long boardId, String title, String content, String category) {
        String accessToken = getSharedPreferences("auth", MODE_PRIVATE).getString("accessToken", null);

        if (accessToken == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 수정된 내용 확인
        Log.d("EditActivity", "Title: " + title + ", Content: " + content + ", Category: " + category);

        // 서버에 보내는 수정 요청 객체 생성
        UpdateBoardRequest request = new UpdateBoardRequest(title, content, category);
        String authorizationHeader = "Bearer " + accessToken;

        boardApiService.updateBoard(authorizationHeader, boardId, request).enqueue(new Callback<BoardDetailResponse>() {
            @Override
            public void onResponse(Call<BoardDetailResponse> call, Response<BoardDetailResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditActivity.this, "게시글이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    finish(); // 성공 후 액티비티 종료
                } else {
                    Toast.makeText(EditActivity.this, "수정 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BoardDetailResponse> call, Throwable t) {
                Toast.makeText(EditActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
