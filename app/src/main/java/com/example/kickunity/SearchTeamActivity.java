package com.example.kickunity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class SearchTeamActivity extends AppCompatActivity {

    private ImageButton backButton;
    private LinearLayout teamListLayout;
    private SharedPreferences sharedPreferences;
    private ArrayList<CreateTeamActivity.Team> teamList; // 팀 정보를 저장할 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_team);

        teamListLayout = findViewById(R.id.teamlist);
        backButton = findViewById(R.id.backButton);

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("TeamPrefs", MODE_PRIVATE);
        loadTeamData(); // 팀 데이터 불러오기

        // 팀 정보를 리스트에 추가
        for (CreateTeamActivity.Team team : teamList) {
            addTeamToList(team);
        }

        // 뒤로 가기 버튼 클릭 리스너
        backButton.setOnClickListener(view -> {
            finish();  // 현재 액티비티를 종료하고 이전 액티비티로 돌아감
        });
    }

    private void loadTeamData() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("teamList", null);
        Type type = new TypeToken<ArrayList<CreateTeamActivity.Team>>() {}.getType();
        teamList = gson.fromJson(json, type);

        if (teamList == null) {
            teamList = new ArrayList<>(); // 데이터가 없으면 새 리스트 생성
        }
    }

    // 팀 정보를 스크롤 뷰에 추가하는 메소드
    private void addTeamToList(CreateTeamActivity.Team team) {
        String teamInfo = String.format("팀명: %s\n창단일: %s\n활동 지역: %s\n연령대: %s\n팀 인원: %s",
                team.teamName, team.foundedDate, team.activityRegion, team.ageGroup, team.teamSize);

        TextView teamTextView = new TextView(this);
        teamTextView.setText(teamInfo);
        teamTextView.setPadding(16, 16, 16, 16);

        // teamlist 레이아웃에 팀 정보 추가
        teamListLayout.addView(teamTextView);
    }
}
