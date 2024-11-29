package com.example.kickunity.Team;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.kickunity.R;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TeamIntroductionModifyActivity extends AppCompatActivity {

    private ImageButton backButton;
    private Button completionButton;
    private EditText TeamDescription;

    private TeamApiService teamApiService;
    private Long teamId; // 팀 ID 저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teamintroduction_modify); // 연령대 수정 레이아웃 연결

        // 뷰 초기화
        backButton = findViewById(R.id.backButton);
        completionButton = findViewById(R.id.Completion_button); // XML ID 확인 후 수정
        TeamDescription = findViewById(R.id.textInput); // XML ID 확인 후 수정

        // Intent로 전달된 teamId 가져오기
        teamId = getIntent().getLongExtra("teamId", -1);
        if (teamId == -1) {
            Toast.makeText(this, "팀 ID가 잘못 전달되었습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Retrofit 초기화
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://15.165.133.129:8080/") // 실제 서버의 base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        teamApiService = retrofit.create(TeamApiService.class);

        // 뒤로가기 버튼 클릭 리스너
        backButton.setOnClickListener(v -> onBackPressed());

        // 액세스 토큰 가져오기
        String accessToken = getAccessTokenFromSharedPreferences();

        if (accessToken == null) {
            return; // 토큰이 없으면 더 이상 진행하지 않음
        }

        // 완료 버튼 클릭 리스너
        completionButton.setOnClickListener(v -> {
            String teamDescription = TeamDescription.getText().toString();
            if (!teamDescription.isEmpty()) {
                changeTeamDescription(teamDescription, accessToken, teamId); // 연령대 수정 요청
            } else {
                Toast.makeText(TeamIntroductionModifyActivity.this, "팀 소개글을 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeTeamDescription(String teamDescription, String accessToken, Long teamId) {
        String authorizationHeader = "Bearer " + accessToken;

        // UpdateTeamRequest 객체 생성 (st와 number 필드 포함)
        UpdateTeamRequest request = new UpdateTeamRequest(teamDescription, 0); // number는 임의로 0 설정

        teamApiService.updateTeamDescription(authorizationHeader, teamId, request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("API Response", "Code: " + response.code() + ", Message: " + response.message());

                // 서버가 200 OK로 응답하면
                if (response.isSuccessful()) {
                    Toast.makeText(TeamIntroductionModifyActivity.this, "팀 소개글이 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show();
                    finish(); // 액티비티 종료
                } else {
                    Log.e("API Error", "Error response code: " + response.code());
                    Toast.makeText(TeamIntroductionModifyActivity.this, "서버 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("API Failure", "Network failure: " + t.getMessage());
                Toast.makeText(TeamIntroductionModifyActivity.this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getAccessTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        String token = sharedPreferences.getString("accessToken", null);
        if (token == null) {
            Toast.makeText(this, "토큰이 없습니다. 다시 로그인 해주세요.", Toast.LENGTH_SHORT).show();
        }
        return token; // 토큰이 없으면 null 반환
    }
}
