package com.example.kickunity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class CreateTeamActivity extends AppCompatActivity {

    private ImageButton backButton;
    private EditText teamNameInput, foundedDateInput, activityRegionInput, ageGroupInput, teamSizeInput;
    private SharedPreferences sharedPreferences;
    private ArrayList<Team> teamList; // 팀 정보를 저장할 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

        teamNameInput = findViewById(R.id.teamNameInput);
        foundedDateInput = findViewById(R.id.foundedDateInput);
        activityRegionInput = findViewById(R.id.activityRegionInput);
        ageGroupInput = findViewById(R.id.ageGroupInput);
        teamSizeInput = findViewById(R.id.teamSizeInput);
        backButton = findViewById(R.id.backButton);

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("TeamPrefs", MODE_PRIVATE);
        loadTeamData(); // 팀 데이터 불러오기

        // 뒤로 가기 버튼 클릭 리스너
        backButton.setOnClickListener(view -> {
            finish();  // 현재 액티비티를 종료하고 이전 액티비티로 돌아감
        });
    }

    public void registerTeam(View view) {
        // 입력된 팀 정보 가져오기
        String teamName = teamNameInput.getText().toString();
        String foundedDate = foundedDateInput.getText().toString();
        String activityRegion = activityRegionInput.getText().toString();
        String ageGroup = ageGroupInput.getText().toString();
        String teamSize = teamSizeInput.getText().toString();

        // 팀 객체 생성 및 리스트에 추가
        Team newTeam = new Team(teamName, foundedDate, activityRegion, ageGroup, teamSize);
        teamList.add(newTeam);
        saveTeamData(); // 데이터 저장

        // 팀 프래그먼트로 돌아가기
        finish();
    }

    private void loadTeamData() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("teamList", null);
        Type type = new TypeToken<ArrayList<Team>>() {}.getType();
        teamList = gson.fromJson(json, type);

        if (teamList == null) {
            teamList = new ArrayList<>(); // 데이터가 없으면 새 리스트 생성
        }
    }

    private void saveTeamData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(teamList);
        editor.putString("teamList", json);
        editor.apply();  // 변경 사항 적용
    }

    // 팀 클래스 정의
    public class Team {
        String teamName;
        String foundedDate;
        String activityRegion;
        String ageGroup;
        String teamSize;

        public Team(String teamName, String foundedDate, String activityRegion, String ageGroup, String teamSize) {
            this.teamName = teamName;
            this.foundedDate = foundedDate;
            this.activityRegion = activityRegion;
            this.ageGroup = ageGroup;
            this.teamSize = teamSize;
        }
    }
    // 특정 팀 정보 삭제하기
    public void deleteTeam(String teamName) {
        for (int i = 0; i < teamList.size(); i++) {
            if (teamList.get(i).teamName.equals(teamName)) {
                teamList.remove(i); // 팀 정보 삭제
                break;
            }
        }
        saveTeamData(); // 데이터 저장
    }

}
