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

        backButton = findViewById(R.id.backButton);
        timeEditText = findViewById(R.id.timeEditText);
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        categorySpinner = findViewById(R.id.categorySpinner);

        // 카테고리 설정
        String[] categories = {"전체 게시판", "축구 게시판", "농구 게시판", "야구 게시판", "기타 스포츠 게시판"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Back button click listener
        backButton.setOnClickListener(view -> {
            finish(); // 현재 Activity 종료
        });

        // 시간 EditText에 TextWatcher 추가
        timeEditText.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) {
                    return;
                }
                isUpdating = true;
                String input = s.toString();

                // ":" 제거하고 숫자만 남기기
                input = input.replace(":", "");
                StringBuilder formattedInput = new StringBuilder();

                // 첫 2자리 시간 입력
                if (input.length() >= 2) {
                    formattedInput.append(input.substring(0, 2));
                    formattedInput.append(":");
                    if (input.length() > 2) {
                        formattedInput.append(input.substring(2));
                    }
                } else {
                    formattedInput.append(input);
                }

                // 최종 입력 제한
                if (formattedInput.length() > 5) {
                    formattedInput.setLength(5);
                }

                timeEditText.setText(formattedInput.toString());
                timeEditText.setSelection(formattedInput.length());

                isUpdating = false;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("title", titleEditText.getText().toString());
            resultIntent.putExtra("content", contentEditText.getText().toString());
            resultIntent.putExtra("category", categorySpinner.getSelectedItem().toString());
            resultIntent.putExtra("time", timeEditText.getText().toString());
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}