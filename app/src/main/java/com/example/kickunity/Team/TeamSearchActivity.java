package com.example.kickunity.Team;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kickunity.Auth.RetrofitClient;
import com.example.kickunity.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamSearchActivity extends AppCompatActivity {

    private static final String TAG = "TeamSearchActivity";

    private RecyclerView recyclerView;
    private EditText searchEditText;
    private TeamAdapter adapter;
    private TeamApiService teamApiService;

    // 액세스 토큰을 담을 변수
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_team);

        // 액세스 토큰을 Intent에서 받아옵니다.
        Intent intent = getIntent();
        accessToken = intent.getStringExtra("accessToken"); // accessToken을 받음

        // 액세스 토큰이 없으면 사용자에게 알림
        if (TextUtils.isEmpty(accessToken)) {
            showToast("액세스 토큰이 유효하지 않습니다.");
            return; // 액세스 토큰이 없으면 아래 API 호출을 하지 않음
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 아이템 클릭 리스너와 가입 요청 리스너 설정
        adapter = new TeamAdapter(
                teamId -> {
                    // 가입 요청 버튼 클릭 시 처리
                    // 여기는 주석 처리하여 기능을 비활성화 합니다.
                    // Toast.makeText(this, "팀 " + teamId + "에 가입 요청", Toast.LENGTH_SHORT).show();
                },
                teamId -> {
                    // 팀 아이템 클릭 시 상세 페이지로 이동
                    openTeamDetailActivity(teamId);
                }
        );
        recyclerView.setAdapter(adapter);

        // API 서비스 초기화
        teamApiService = RetrofitClient.getTeamApiService();

        setupSearchUI();
        fetchTeams(""); // 초기 팀 목록 로드

        // 뒤로 가기 버튼
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    // 검색 UI 초기화
    private void setupSearchUI() {
        searchEditText = findViewById(R.id.searchEditText);

        ImageButton searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> {
            String keyword = searchEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(keyword)) {
                searchTeamsByName(keyword);
            } else {
                showToast("검색어를 입력하세요.");
            }
        });

        // 키보드 Enter 시에도 검색 처리
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String keyword = searchEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(keyword)) {
                    searchTeamsByName(keyword);
                } else {
                    showToast("검색어를 입력하세요.");
                }
                return true;
            }
            return false;
        });
    }

    // 팀 이름으로 팀 검색
    private void searchTeamsByName(String teamName) {
        if (teamName == null || teamName.trim().isEmpty()) {
            Log.e(TAG, "팀 이름 값이 유효하지 않습니다.");
            showToast("팀 이름을 입력하세요.");
            return;
        }

        // Retrofit API 호출
        Call<List<TeamSummaryResponse>> call = teamApiService.searchTeamByName(teamName.trim());
        call.enqueue(new Callback<List<TeamSummaryResponse>>() {
            @Override
            public void onResponse(Call<List<TeamSummaryResponse>> call, Response<List<TeamSummaryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TeamSummaryResponse> teams = response.body();
                    if (teams.isEmpty()) {
                        // 검색 결과 없음
                        Log.d(TAG, "검색 결과 없음.");
                        showToast("검색 결과가 없습니다.");
                    } else {
                        // 팀 목록 업데이트
                        adapter.setTeamList(teams);  // RecyclerView 업데이트
                        Log.d(TAG, "검색 성공: " + teams.size() + "개 팀 로드됨.");
                    }
                } else if (response.code() == 404) {
                    // 팀이 존재하지 않는 경우
                    Log.d(TAG, "해당 이름을 가진 팀이 존재하지 않습니다.");
                    showToast("해당 이름을 가진 팀이 없습니다.");
                } else {
                    handleError(response);  // 서버 에러 처리
                }
            }

            @Override
            public void onFailure(Call<List<TeamSummaryResponse>> call, Throwable t) {
                Log.e(TAG, "네트워크 요청 실패: " + t.getMessage(), t);
                showToast("네트워크 오류가 발생했습니다.");
            }
        });
    }

    // 기본적으로 팀 목록 불러오기 (검색어 없을 때)
    private void fetchTeams(String query) {
        Toast.makeText(this, "팀 목록을 불러오는 중입니다...", Toast.LENGTH_SHORT).show();
        teamApiService.searchTeamByName(query)
                .enqueue(new Callback<List<TeamSummaryResponse>>() {
                    @Override
                    public void onResponse(Call<List<TeamSummaryResponse>> call, Response<List<TeamSummaryResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<TeamSummaryResponse> teamList = response.body();
                            if (!teamList.isEmpty()) {
                                adapter.setTeamList(teamList);
                            } else {
                                Toast.makeText(TeamSearchActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(TeamSearchActivity.this, "팀 목록 로드 실패.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TeamSummaryResponse>> call, Throwable t) {
                        Toast.makeText(TeamSearchActivity.this, "오류 발생: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 서버 오류 처리
    private void handleError(Response<List<TeamSummaryResponse>> response) {
        // 응답 코드에 따라 적절히 처리
        int statusCode = response.code();
        String errorMessage = "서버 오류 발생. 상태 코드: " + statusCode;
        Log.e(TAG, errorMessage);
        showToast(errorMessage);
    }

    // Toast 메시지 출력
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // TeamSearchActivity에서
    private void openTeamDetailActivity(long teamId) {
        if (accessToken != null) {
            Intent intent = new Intent(TeamSearchActivity.this, TeamDetailActivity.class);
            intent.putExtra("teamId", teamId); // teamId를 Intent에 추가
            intent.putExtra("accessToken", accessToken); // accessToken을 함께 전달
            Log.d(TAG, "openTeamDetailActivity 호출: teamId = " + teamId + ", accessToken = " + accessToken); // 디버깅 로그 추가
            startActivity(intent);  // TeamDetailActivity로 이동
        } else {
            showToast("액세스 토큰이 없습니다. 다시 로그인 해주세요.");
        }
    }
}
