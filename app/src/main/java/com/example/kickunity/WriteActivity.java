package com.example.kickunity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WriteActivity extends AppCompatActivity {

    private ImageButton backButton;
    private EditText titleEditText;
    private EditText contentEditText;
    private Spinner categorySpinner;
    private EditText timeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        // UI 요소 초기화
        backButton = findViewById(R.id.backButton);
        timeEditText = findViewById(R.id.timeEditText);
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        Button submitButton = findViewById(R.id.submitButton);

        if (backButton == null || timeEditText == null || titleEditText == null || contentEditText == null || categorySpinner == null || submitButton == null) {
            Toast.makeText(this, "UI 요소 초기화에 실패했습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 카테고리 설정
        String[] categories = {"전체 게시판", "축구 게시판", "농구 게시판", "야구 게시판", "기타 스포츠 게시판"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // 전달받은 기본 카테고리 설정
        String defaultCategory = getIntent().getStringExtra("defaultCategory");
        if (defaultCategory != null) {
            int position = adapter.getPosition(defaultCategory);
            if (position >= 0) {
                categorySpinner.setSelection(position);
            }
        }

        // Back 버튼 리스너
        backButton.setOnClickListener(view -> finish());

        // 시간 입력 형식 제한
        timeEditText.addTextChangedListener(new TimeTextWatcher());

        // 제출 버튼 클릭 리스너
        submitButton.setOnClickListener(v -> handleSubmit());
    }

    /**
     * 게시글 제출 처리 메서드
     */
    private void handleSubmit() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();
        String time = timeEditText.getText().toString().trim();
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
        if (time.isEmpty() || !time.matches("^\\d{2}:\\d{2}$")) {
            Toast.makeText(this, "시간을 올바른 형식으로 입력하세요. (예: 12:30)", Toast.LENGTH_SHORT).show();
            return;
        }

        // 결과 전달
        Intent resultIntent = new Intent();
        resultIntent.putExtra("title", title);
        resultIntent.putExtra("content", content);
        resultIntent.putExtra("category", category);
        resultIntent.putExtra("time", time);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    /**
     * 시간 입력 형식을 제한하는 TextWatcher
     */
    private class TimeTextWatcher implements TextWatcher {
        private boolean isUpdating = false;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (isUpdating) {
                return;
            }
            isUpdating = true;

            String input = s.toString().replace(":", ""); // ":" 제거
            StringBuilder formatted = new StringBuilder();

            if (input.length() >= 2) {
                formatted.append(input.substring(0, 2)).append(":");
                if (input.length() > 2) {
                    formatted.append(input.substring(2));
                }
            } else {
                formatted.append(input);
            }

            // 최대 5자까지만 허용
            if (formatted.length() > 5) {
                formatted.setLength(5);
            }

            timeEditText.setText(formatted.toString());
            timeEditText.setSelection(formatted.length());
            isUpdating = false;
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }
}
