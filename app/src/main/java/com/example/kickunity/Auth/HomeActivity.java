package com.example.kickunity.Auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.kickunity.Board.BaseballBoardFragment;
import com.example.kickunity.Board.BasketballBoardFragment;
import com.example.kickunity.Board.OtherSportsBoardFragment;
import com.example.kickunity.Board.PostViewModel;
import com.example.kickunity.Board.SoccerBoardFragment;
import com.example.kickunity.Board.WriteActivity;
import com.example.kickunity.Chat.ChatFragment;
import com.example.kickunity.Profile.ProfileFragment;
import com.example.kickunity.R;
import com.example.kickunity.Team.CreateTeamActivity;
import com.example.kickunity.Team.TeamApiService;
import com.example.kickunity.Team.TeamFragment;
import com.example.kickunity.Team.TeamSearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    private FloatingActionButton fabCreatePost;
    private FloatingActionButton fabTeamAction;
    private View cardOption1;
    private View cardOption2;
    private boolean isCardVisible = false;
    private boolean isFabMainVisible = false; // 플로팅 버튼 상태 추가
    private static final int WRITE_REQUEST_CODE = 1;
    private PostViewModel postViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabCreatePost = findViewById(R.id.fabCreatePost);
        fabTeamAction = findViewById(R.id.fabTeamAction);
        cardOption1 = findViewById(R.id.card_option_1);
        cardOption2 = findViewById(R.id.card_option_2);

        fabCreatePost.hide();
        fabTeamAction.hide(); // 초기에는 플로팅 버튼 숨기기
        hideCardOptions(); // 카드 뷰 초기 숨기기

        fabCreatePost.setOnClickListener(view -> {
            String category = "ALL"; // 기본 카테고리

            // 현재 활성화된 프래그먼트 확인
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);

            // 프래그먼트에 따라 카테고리 설정
            if (currentFragment instanceof SoccerBoardFragment) {
                category = "SOCCER";
            } else if (currentFragment instanceof BasketballBoardFragment) {
                category = "BASKETBALL";
            } else if (currentFragment instanceof BaseballBoardFragment) {
                category = "BASEBALL";
            } else if (currentFragment instanceof OtherSportsBoardFragment) {
                category = "ETC";
            }

            // 카테고리 값만 WriteActivity로 전달
            Intent intent = new Intent(HomeActivity.this, WriteActivity.class);
            intent.putExtra("defaultCategory", category); // 카테고리만 전달
            startActivityForResult(intent, WRITE_REQUEST_CODE);
        });


        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        loadFragment(new HomeFragment()); // HomeFragment를 기본으로 로드

        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
                fabCreatePost.hide();
                hideCardOptions(); // 카드 뷰 숨기기
                fabTeamAction.hide(); // nav_home 선택 시 플로팅 버튼 숨기기
            } else if (id == R.id.nav_team) {
                checkTeamStatus(); // 서버에서 팀 상태 확인
            }  else if (id == R.id.nav_chat) {
                selectedFragment = new ChatFragment(); // ChatFragment 추가
                fabCreatePost.hide();
                hideCardOptions(); // 카드 뷰 숨기기
                fabTeamAction.hide(); // nav_chat 선택 시 플로팅 버튼 숨기기
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment(); // ProfileFragment 추가
                fabCreatePost.hide();
                hideCardOptions(); // 카드 뷰 숨기기
                fabTeamAction.hide(); // nav_profile 선택 시 플로팅 버튼 숨기기
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });


        fabTeamAction.setOnClickListener(view -> {
            hideFabMain(); // fab_main 클릭 시 플로팅 버튼 숨기기
        });

        // 카드 옵션 클릭 리스너 설정
        cardOption1.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, CreateTeamActivity.class);
            startActivity(intent); // CreateTeamActivity 열기
        });

        cardOption2.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, TeamSearchActivity.class);
            startActivity(intent); // SearchTeamActivity 열기
        });
    }

    private void checkTeamStatus() {
        // SharedPreferences에서 액세스 토큰을 가져옵니다.
        String accessToken = getAccessTokenFromSharedPreferences();
        if (accessToken == null) {
            Toast.makeText(this, "토큰이 없어서 팀 상태를 확인할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrofit 객체 생성
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://15.165.133.129:8080/") // 서버 URL
                .addConverterFactory(GsonConverterFactory.create()) // Gson 변환기 추가
                .build();

        // TeamApi 인터페이스 구현체 생성
        TeamApiService teamApi = retrofit.create(TeamApiService.class);

        // Authorization Header 생성
        String authorizationHeader = "Bearer " + accessToken;

        // 서버 요청
        Call<Long> call = teamApi.getUserTeamStatus(authorizationHeader);

        // 비동기 요청 처리
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.isSuccessful()) {
                    Long teamId = response.body(); // Long 타입으로 응답 받음
                    if (teamId != null) {
                        // 팀 ID가 있는 경우
                        Log.d("TeamStatus", "사용자의 팀 ID: " + teamId);

                        // 팀 프래그먼트로 이동
                        Bundle bundle = new Bundle();
                        bundle.putLong("teamId", teamId);
                        TeamFragment teamFragment = new TeamFragment();
                        teamFragment.setArguments(bundle);
                        loadFragment(teamFragment);
                    } else {
                        // 팀 정보가 없을 경우
                        Log.d("TeamStatus", "사용자는 소속된 팀이 없습니다.");
                        showFabMain();
                        toggleCardOptions();
                    }
                } else if (response.code() == 404) {
                    // 사용자가 소속된 팀이 없는 경우
                    Log.d("TeamStatus", "사용자는 소속된 팀이 없습니다.");
                    showFabMain();
                    toggleCardOptions();
                } else {
                    // 다른 오류 상황 처리
                    Log.e("TeamStatus", "서버 응답 오류: " + response.code() + " - " + response.message());
                    Toast.makeText(HomeActivity.this, "서버 오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                // 네트워크 오류 또는 예외 처리
                Log.e("NetworkError", "네트워크 오류 발생", t);
                Toast.makeText(HomeActivity.this, "네트워크 오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showFabMain() {
        fabTeamAction.show();
        isFabMainVisible = true;
    }

    private void hideFabMain() {
        fabTeamAction.hide();
        isFabMainVisible = false;
        hideCardOptions(); // fab_main 숨길 때 카드 뷰도 숨기기
    }

    private void toggleCardOptions() {
        if (isCardVisible) {
            hideCardOptions();
        } else {
            showCardOptions();
        }
    }

    private void showCardOptions() {
        cardOption1.setVisibility(View.VISIBLE);
        cardOption2.setVisibility(View.VISIBLE);
        isCardVisible = true;
    }

    private void hideCardOptions() {
        cardOption1.setVisibility(View.GONE);
        cardOption2.setVisibility(View.GONE);
        isCardVisible = false;
    }

    // 프래그먼트를 로드하는 메서드
    private void loadFragment(Fragment fragment) {
        // FragmentTransaction을 이용하여 프래그먼트를 교체
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);  // 백스택에 추가하여 뒤로 가기 시 이전 프래그먼트로 돌아갈 수 있게 함
        transaction.commit();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WRITE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra("title");
            String content = data.getStringExtra("content");
            String category = data.getStringExtra("category");
            String time = data.getStringExtra("time");

            postViewModel.addPost(title, content, category, time);
        }
    }

    private String getAccessTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        String token = sharedPreferences.getString("accessToken", null);
        if (token == null) {
            // 토큰이 없으면 로그인 화면으로 이동하거나 예외 처리
            Toast.makeText(this, "토큰이 없습니다. 다시 로그인 해주세요.", Toast.LENGTH_SHORT).show();
        }
        return token;  // 토큰이 없으면 null 반환
    }


}