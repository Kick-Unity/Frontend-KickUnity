package com.example.kickunity.Team;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kickunity.Auth.RetrofitClient;
import com.example.kickunity.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamDetailActivity extends AppCompatActivity {

    private Long teamId;
    private String accessToken;

    private TextView teamName, teamCategory, teamFoundingYear, teamRegion, teamAgeGroup, teamMembers, teamIntroduction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_detail);

        // UI 초기화
        teamName = findViewById(R.id.teamName);
        teamCategory = findViewById(R.id.teamCategoryTextView);
        teamFoundingYear = findViewById(R.id.editTextFoundingYear);
        teamRegion = findViewById(R.id.editTextActivityRegion);
        teamAgeGroup = findViewById(R.id.editTextAgeGroup);
        teamMembers = findViewById(R.id.editTextTeamMembers);
        teamIntroduction = findViewById(R.id.editTextClubDescription);

        // 뒤로 가기 버튼 설정
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // 팀 ID와 액세스 토큰 가져오기
        if (getIntent() != null) {
            teamId = getIntent().getLongExtra("teamId", -1); // teamId
            accessToken = getIntent().getStringExtra("accessToken"); // accessToken
        }

        // 액세스 토큰이 없는 경우 처리
        if (TextUtils.isEmpty(accessToken)) {
            showToast("액세스 토큰이 유효하지 않습니다.");
            return;  // 액세스 토큰이 없으면 상세 정보를 불러오지 않음
        }

        // 팀 ID가 유효한지 확인하고, 액세스 토큰이 있을 때만 팀 정보 로드
        if (teamId != -1) {
            loadTeamDetails(teamId, accessToken);  // 팀 정보 로드
        } else {
            showToast("팀 ID가 유효하지 않습니다.");
        }
    }

    // 서버에서 팀 상세 정보 불러오기
    private void loadTeamDetails(Long teamId, String accessToken) {
        // API 요청 준비
        TeamApiService teamApiService = RetrofitClient.getTeamApiService();
        Call<TeamDetailResponse> call = teamApiService.getTeamDetailWithToken(teamId, "Bearer " + accessToken);

        // API 요청 보내기
        call.enqueue(new Callback<TeamDetailResponse>() {
            @Override
            public void onResponse(Call<TeamDetailResponse> call, Response<TeamDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 성공적으로 팀 정보를 받아오면 데이터를 UI에 반영
                    TeamDetailResponse teamDetails = response.body();
                    displayTeamDetails(teamDetails);
                } else {
                    showToast("팀 정보를 불러오는데 실패했습니다.");
                }
            }

            @Override
            public void onFailure(Call<TeamDetailResponse> call, Throwable t) {
                // 네트워크 오류 시
                showToast("네트워크 오류: " + t.getMessage());
            }
        });
    }

    // UI에 팀 상세 정보를 표시하는 메소드
    private void displayTeamDetails(TeamDetailResponse teamDetails) {
        teamName.setText(teamDetails.getTeamName());
        teamCategory.setText(teamDetails.getTeamCategory());
        teamFoundingYear.setText(teamDetails.getTeamStartDate());
        teamRegion.setText(teamDetails.getTeamRegion());
        teamAgeGroup.setText(teamDetails.getTeamAge());
        teamMembers.setText(String.valueOf(teamDetails.getTeamSize()));
        teamIntroduction.setText(teamDetails.getTeamDescription());
    }

    // 토스트 메시지 출력
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
