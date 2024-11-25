package com.example.kickunity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

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
                selectedFragment = new TeamFragment(); // TeamFragment 추가
                fabCreatePost.hide(); // 기본 FAB 숨기기
                showFabMain(); // 플로팅 버튼 보이기
                toggleCardOptions(); // 카드 뷰 보이기
            } else if (id == R.id.nav_chat) {
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
            Intent intent = new Intent(HomeActivity.this, SearchTeamActivity.class);
            startActivity(intent); // SearchTeamActivity 열기
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

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
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

}