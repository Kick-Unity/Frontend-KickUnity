package com.example.kickunity.Team;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kickunity.Auth.RetrofitClient;
import com.example.kickunity.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTeamActivity extends AppCompatActivity {

    private EditText teamNameInput, foundedDateInput, activityRegionInput, ageGroupInput, teamSizeInput, teamCategoryInput, teamDescriptionInput;
    private TeamApiService teamApiService;  // TeamApiService 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

        // UI 요소 초기화
        teamNameInput = findViewById(R.id.teamNameInput);
        foundedDateInput = findViewById(R.id.foundedDateInput);
        activityRegionInput = findViewById(R.id.activityRegionInput);
        ageGroupInput = findViewById(R.id.ageGroupInput);
        teamSizeInput = findViewById(R.id.teamSizeInput);
        teamCategoryInput = findViewById(R.id.teamCategoryInput);
        teamDescriptionInput = findViewById(R.id.teamDescriptionInput);

        teamApiService = RetrofitClient.getTeamApiService();

        // 뒤로가기 버튼 클릭 리스너 추가
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();  // 뒤로가기 버튼을 클릭하면 이전 화면으로 돌아감
            }
        });
    }

    public void registerTeam(View view) {
        // SharedPreferences에서 토큰을 가져옵니다.
        String accessToken = getAccessTokenFromSharedPreferences();
        if (accessToken == null) {
            Toast.makeText(this, "토큰이 없어서 팀 상태를 확인할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 입력된 값 가져오기
        String teamName = teamNameInput.getText().toString().trim();
        String foundedDate = foundedDateInput.getText().toString().trim();
        String activityRegion = activityRegionInput.getText().toString().trim();
        String ageGroup = ageGroupInput.getText().toString().trim();
        String teamSize = teamSizeInput.getText().toString().trim();
        String teamCategory = teamCategoryInput.getText().toString().trim();
        String teamDescription = teamDescriptionInput.getText().toString().trim();

        // 유효성 검사
        if (teamName.isEmpty() || foundedDate.isEmpty() || activityRegion.isEmpty() ||
                ageGroup.isEmpty() || teamSize.isEmpty() || teamCategory.isEmpty() ||
                teamDescription.isEmpty()) {
            Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 팀 생성 요청 데이터 준비
        AddTeamRequest request = new AddTeamRequest(
                teamName,
                teamCategory,
                foundedDate,
                activityRegion,
                ageGroup,
                Integer.parseInt(teamSize),
                teamDescription
        );

        // ProgressDialog 표시
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("팀을 생성 중입니다...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Retrofit API 서비스 호출
        Call<Long> call = teamApiService.createTeam("Bearer " + accessToken, request); // 수정된 부분
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    Long teamId = response.body();
                    Toast.makeText(CreateTeamActivity.this, "팀 생성 성공! 팀 ID: " + teamId, Toast.LENGTH_SHORT).show();
                    // 성공적으로 팀을 생성하면 현재 액티비티 종료
                    finish();
                } else {
                    Toast.makeText(CreateTeamActivity.this, "팀 생성 실패! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(CreateTeamActivity.this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getAccessTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", null);
    }
}
