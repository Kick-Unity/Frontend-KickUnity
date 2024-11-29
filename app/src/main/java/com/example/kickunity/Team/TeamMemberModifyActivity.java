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

public class TeamMemberModifyActivity extends AppCompatActivity {

    private ImageButton backButton;
    private Button completionButton;
    private EditText TeamSize;

    private TeamApiService teamApiService;
    private Long teamId; // 팀 ID 저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teammember_modify); // 연령대 수정 레이아웃 연결

        // 뷰 초기화
        backButton = findViewById(R.id.backButton);
        completionButton = findViewById(R.id.Completion_button); // XML ID 확인 후 수정
        TeamSize = findViewById(R.id.textInput); // XML ID 확인 후 수정

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
            // EditText에서 입력받은 값을 바로 int로 변환
            String teamSizeInput = TeamSize.getText().toString().trim();

            // 입력값이 비어 있지 않으면 int로 변환
            if (!teamSizeInput.isEmpty()) {
                try {
                    int teamSize = Integer.parseInt(teamSizeInput); // 입력값을 int로 변환
                    changeTeamSize(teamSize, accessToken, teamId); // 팀 인원 수정 요청
                } catch (NumberFormatException e) {
                    Toast.makeText(TeamMemberModifyActivity.this, "유효한 숫자를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(TeamMemberModifyActivity.this, "팀 인원을 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeTeamSize(int teamSize, String accessToken, Long teamId) {
        String authorizationHeader = "Bearer " + accessToken;

        // UpdateTeamRequest 객체 생성 (teamSize만 포함)
        UpdateTeamRequest request = new UpdateTeamRequest(null, teamSize); // number는 임의로 null 처리

        teamApiService.updateTeamSize(authorizationHeader, teamId, request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("API Response", "Code: " + response.code() + ", Message: " + response.message());

                // 서버가 200 OK로 응답하면
                if (response.isSuccessful()) {
                    Toast.makeText(TeamMemberModifyActivity.this, "팀 인원이 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show();
                    finish(); // 액티비티 종료
                } else {
                    Log.e("API Error", "Error response code: " + response.code());
                    Toast.makeText(TeamMemberModifyActivity.this, "서버 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("API Failure", "Network failure: " + t.getMessage());
                Toast.makeText(TeamMemberModifyActivity.this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
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
