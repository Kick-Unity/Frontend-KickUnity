package com.example.kickunity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

        // 팀명 텍스트 뷰
        TextView teamNameTextView = new TextView(this);
        teamNameTextView.setText(team.teamName);
        teamNameTextView.setTextSize(17); // 팀명 글씨 크기
        teamNameTextView.setTypeface(null, Typeface.BOLD); // 팀명 굵게 설정
        teamNameTextView.setTextColor(Color.BLACK); // 글씨 색 설정

        // 활동 지역 텍스트 뷰
        TextView activityRegionTextView = new TextView(this);
        activityRegionTextView.setText("활동 지역 : " + team.activityRegion);
        activityRegionTextView.setTextSize(11); // 활동 지역 작은 글씨
        activityRegionTextView.setTextColor(Color.DKGRAY); // 연한 글씨 색 설정

        // 연령대 텍스트 뷰
        TextView ageGroupTextView = new TextView(this);
        ageGroupTextView.setText("연령대 : " + team.ageGroup);
        ageGroupTextView.setTextSize(11); // 연령대 글씨 크기
        ageGroupTextView.setTextColor(Color.BLACK); // 기본 글씨 색 설정

        // 팀 프로필 이미지를 위한 ImageView 생성 (임시 이미지 설정)
        ImageView teamProfileImageView = new ImageView(this);
        teamProfileImageView.setImageResource(R.drawable.profile); // 임시 프로필 이미지
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(100, 100);
        imageParams.setMargins(dpToPx(15), 30, 0, 30); // 이미지 좌우 여백 설정 (25dp 왼쪽, 20dp 오른쪽)
        teamProfileImageView.setLayoutParams(imageParams); // 이미지 크기 설정
        teamProfileImageView.setScaleType(ImageView.ScaleType.CENTER_CROP); // 이미지 크롭 설정

        // 팀명과 활동 지역을 포함하는 수직 레이아웃
        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL); // 수직 배치
        textLayout.addView(teamNameTextView);
        textLayout.addView(activityRegionTextView);

        // 수평으로 배치하는 레이아웃
        LinearLayout horizontalLayout = new LinearLayout(this);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL); // 수평 배치
        horizontalLayout.setGravity(Gravity.CENTER_VERTICAL); // 수직 중앙 정렬

        // 텍스트 레이아웃 마진 및 가변 크기 설정
        LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        textLayoutParams.setMargins(dpToPx(15), 0, 0, 0); // 텍스트 왼쪽에 20dp 마진
        textLayout.setLayoutParams(textLayoutParams); // 텍스트 레이아웃 가변 크기 설정

        // 연령대 레이아웃 패딩 및 정렬 설정
        LinearLayout.LayoutParams ageGroupLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        ageGroupLayoutParams.setMargins(0, 0, 0, 0);
        ageGroupTextView.setLayoutParams(ageGroupLayoutParams);


        // 가입 신청 버튼 생성
        Button applyButton = new Button(this);
        applyButton.setText("가입 신청");
        applyButton.setTextSize(12);
        applyButton.setTextColor(Color.WHITE); // 버튼 글씨 색 설정
        // 버튼 배경 설정을 위한 GradientDrawable 생성
        GradientDrawable buttonBackground = new GradientDrawable();
        buttonBackground.setColor(Color.parseColor("#393E46")); // 버튼 배경 색을 #393E46으로 설정
        buttonBackground.setCornerRadius(dpToPx(20)); // 버튼 모서리 둥글게 (15dp)
        // 버튼에 배경 적용
        applyButton.setBackground(buttonBackground);
        // 버튼 레이아웃 설정
        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.setMargins(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10)); // 버튼 마진 설정
        applyButton.setLayoutParams(buttonLayoutParams);




        // 이미지 추가
        horizontalLayout.addView(teamProfileImageView); // 이미지 추가
        horizontalLayout.addView(textLayout); // 팀명 및 활동 지역 추가
        horizontalLayout.addView(ageGroupTextView); // 연령대 추가
        horizontalLayout.addView(applyButton);

        // teamlist 레이아웃에 최종 팀 정보 레이아웃 추가
        teamListLayout.addView(horizontalLayout);
    }

    // dp 값을 px로 변환하는 메소드
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }




}