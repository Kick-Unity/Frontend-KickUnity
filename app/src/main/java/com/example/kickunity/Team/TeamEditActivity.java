package com.example.kickunity.Team;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kickunity.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TeamEditActivity extends AppCompatActivity {

    private ImageButton backButton;
    private TextView textTeamName;
    private Button teamNameChange, teamCategoryChange, foundingYearChange, activityRegionChange, ageGroupChange, teamMembersChange, teamIntroductionChange;
    private LinearLayout addTeamMemberButton, deleteTeamMemberButton;

    private Long teamId; // 팀 ID 저장
    private Retrofit retrofit;
    private TeamApiService teamApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_team);

        backButton = findViewById(R.id.backButton);
        textTeamName = findViewById(R.id.textTeamName);
        teamNameChange = findViewById(R.id.teamName_change_button);
        teamCategoryChange = findViewById(R.id.teamCategory_change_button);
        foundingYearChange = findViewById(R.id.foundingYear_change_button);
        activityRegionChange = findViewById(R.id.activityRegion_change_button);
        ageGroupChange = findViewById(R.id.ageGroup_change_button);
        teamMembersChange = findViewById(R.id.teamMembers_change_button);
        teamIntroductionChange = findViewById(R.id.teamIntroduction_change_button);

        // 팀 ID를 Intent로 받아오기
        Intent intent = getIntent();
        if (intent != null) {
            teamId = intent.getLongExtra("teamId", -1);
        }

        if (teamId == -1) {
            Toast.makeText(this, "팀 ID가 잘못 전달되었습니다.", Toast.LENGTH_SHORT).show();
            finish(); // 잘못된 경우 Activity 종료
            return;
        }

        // Retrofit 초기화
        retrofit = new Retrofit.Builder()
                .baseUrl("http://15.165.133.129:8080/") // 서버 주소
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        teamApiService = retrofit.create(TeamApiService.class);

        // 팀 상세 정보 가져오기
        fetchTeamDetail(teamId, getAccessTokenFromSharedPreferences());

        // Back 버튼 클릭 리스너 설정
        backButton.setOnClickListener(view -> finish());

        // 버튼 클릭 리스너 설정
        setupButtonClickListeners();

        // 팀원 추가 및 삭제 버튼 설정
        addTeamMemberButton = findViewById(R.id.addTeamMemberButton);
        addTeamMemberButton.setOnClickListener(v -> showAddMemberDialog());

        deleteTeamMemberButton = findViewById(R.id.deleteTeamMemberButton);
        deleteTeamMemberButton.setOnClickListener(v -> showDeleteMemberDialog());
    }

    private void fetchTeamDetail(Long teamId, String accessToken) {
        if (accessToken == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<TeamDetailResponse> call = teamApiService.getTeamDetailWithToken(teamId, "Bearer " + accessToken);

        call.enqueue(new Callback<TeamDetailResponse>() {
            @Override
            public void onResponse(Call<TeamDetailResponse> call, Response<TeamDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TeamDetailResponse teamDetail = response.body();
                    textTeamName.setText(teamDetail.getTeamName());
                } else {
                    Toast.makeText(TeamEditActivity.this, "팀 정보를 불러올 수 없습니다. 상태 코드: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TeamDetailResponse> call, Throwable t) {
                Toast.makeText(TeamEditActivity.this, "네트워크 오류가 발생했습니다. " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getAccessTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", null);
    }

    private void setupButtonClickListeners() {
        teamNameChange.setOnClickListener(v -> navigateToActivity(TeamNameModifyActivity.class));
        teamCategoryChange.setOnClickListener(v -> navigateToActivity(TeamCategoryModifyActivity.class));
        foundingYearChange.setOnClickListener(v -> navigateToActivity(FoundingYearModifyActivity.class));
        activityRegionChange.setOnClickListener(v -> navigateToActivity(RegionModifyActivity.class));
        ageGroupChange.setOnClickListener(v -> navigateToActivity(AgeGroupModifyActivity.class));
        teamMembersChange.setOnClickListener(v -> navigateToActivity(TeamMemberModifyActivity.class));
        teamIntroductionChange.setOnClickListener(v -> navigateToActivity(TeamIntroductionModifyActivity.class));
    }

    private void navigateToActivity(Class<?> activityClass) {
        Intent intent = new Intent(TeamEditActivity.this, activityClass);
        intent.putExtra("teamId", teamId); // teamId 전달
        startActivity(intent);
    }

    private void showAddMemberDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 제목 설정
        builder.setTitle("팀원 추가");

        // 이메일 입력을 위한 EditText 생성
        final android.widget.EditText emailInput = new android.widget.EditText(this);
        emailInput.setHint("팀원의 이메일을 입력하세요");
        emailInput.setHintTextColor(Color.parseColor("#757575")); // 회색 (#757575)
        emailInput.setTextColor(Color.BLACK); // 텍스트 색상 설정 (검정색)
        emailInput.setPadding(40, 30, 40, 30); // 내부 여백 추가
        emailInput.setBackground(getDrawable(R.drawable.edittext_background)); // 배경을 설정 (뒤에서 정의한 drawable)

        // 이메일 입력을 위한 레이아웃 설정
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 0, 50, 20); // 다이얼로그의 양옆 여백 설정
        layout.addView(emailInput);

        // 다이얼로그에 레이아웃 적용
        builder.setView(layout);

        // 추가 버튼 클릭 리스너 설정
        builder.setPositiveButton("추가", (dialog, which) -> {
            String memberEmail = emailInput.getText().toString().trim();
            if (!memberEmail.isEmpty()) {
                addMember(memberEmail);  // 이메일을 서버로 전달
            } else {
                Toast.makeText(TeamEditActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // 취소 버튼 클릭 리스너 설정
        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

        // 다이얼로그 생성
        AlertDialog dialog = builder.create();
        dialog.show();

        // 버튼 스타일 설정
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.parseColor("#0000FF")); // 추가 버튼 텍스트 색상 변경 (파란색)

        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.parseColor("#FF0000")); // 취소 버튼 텍스트 색상 변경 (빨간색)
    }


    private void showDeleteMemberDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 제목 설정
        builder.setTitle("팀원 삭제");

        // 이메일 입력을 위한 EditText 생성
        final android.widget.EditText emailInput = new android.widget.EditText(this);
        emailInput.setHint("삭제할 팀원의 이메일을 입력하세요");
        emailInput.setHintTextColor(Color.parseColor("#757575")); // 회색 (#757575)
        emailInput.setTextColor(Color.BLACK); // 텍스트 색상 설정 (검정색)
        emailInput.setPadding(40, 30, 40, 30); // 내부 여백 추가
        emailInput.setBackground(getDrawable(R.drawable.edittext_background)); // 배경을 설정 (뒤에서 정의한 drawable)

        // 이메일 입력을 위한 레이아웃 설정
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 0, 50, 20); // 다이얼로그의 양옆 여백 설정
        layout.addView(emailInput);

        // 다이얼로그에 레이아웃 적용
        builder.setView(layout);

        // 삭제 버튼 클릭 리스너 설정
        builder.setPositiveButton("삭제", (dialog, which) -> {
            String memberEmail = emailInput.getText().toString().trim();
            if (!memberEmail.isEmpty()) {
                deleteMemberFromTeam(memberEmail);  // 이메일을 서버로 전달하여 팀원 삭제
            } else {
                Toast.makeText(TeamEditActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // 취소 버튼 클릭 리스너 설정
        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

        // 다이얼로그 생성
        AlertDialog dialog = builder.create();
        dialog.show();

        // 버튼 스타일 설정
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.parseColor("#FF0000")); // 삭제 버튼 텍스트 색상 변경 (빨간색)

        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.parseColor("#0000FF")); // 취소 버튼 텍스트 색상 변경 (파란색)
    }


    // 팀원 추가 API 호출
    private void addMember(String memberEmail) {
        String accessToken = getAccessTokenFromSharedPreferences();
        if (accessToken == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<String> call = teamApiService.addMember("Bearer " + accessToken, teamId, memberEmail);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(TeamEditActivity.this, "팀원 추가 완료: " + response.body(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TeamEditActivity.this, "팀원 추가 실패. 상태 코드: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(TeamEditActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 팀원 삭제 API 호출
    private void deleteMemberFromTeam(String memberEmail) {
        String accessToken = getAccessTokenFromSharedPreferences();
        if (accessToken == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<String> call = teamApiService.removeMember("Bearer " + accessToken, teamId, memberEmail);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(TeamEditActivity.this, "팀원 삭제 완료: " + response.body(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TeamEditActivity.this, "팀원 삭제 실패. 상태 코드: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(TeamEditActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
