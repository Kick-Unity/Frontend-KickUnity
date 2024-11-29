package com.example.kickunity.Team;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
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

    private RecyclerView recyclerView;
    private EditText searchEditText;
    private TeamAdapter adapter;
    private TeamApiService teamApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_team);

        // 초기화
        recyclerView = findViewById(R.id.recyclerView);
        searchEditText = findViewById(R.id.search);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TeamAdapter(teamId ->
                Toast.makeText(TeamSearchActivity.this, "Clicked Team ID: " + teamId, Toast.LENGTH_SHORT).show()
        );
        recyclerView.setAdapter(adapter);

        // API 서비스 초기화
        teamApiService = RetrofitClient.getTeamApiService();

        // 초기 팀 목록 로드
        fetchTeams("");

        // 검색 필드 이벤트 처리
        setupSearchListener();

        // 뒤로 가기 버튼 설정
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish()); // 액티비티 종료
    }

    // 검색 필드에서 입력 후 검색 실행
    private void setupSearchListener() {
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String query = searchEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(query)) {
                    fetchTeams(query);
                } else {
                    Toast.makeText(this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });
    }

    // 팀 검색 API 호출
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
}
