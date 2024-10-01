package com.example.kickunity;

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

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    private FloatingActionButton fab;
    private FloatingActionButton fabMain;
    private View cardOption1;
    private View cardOption2;
    private boolean isCardVisible = false;
    private boolean isFabMainVisible = false; // 플로팅 버튼 상태 추가
    private static final int WRITE_REQUEST_CODE = 1;
    private PostViewModel postViewModel;

    /*
    CheckBox checkBoxTerms = findViewById(R.id.checkBoxTerms);
    CheckBox checkBoxPrivacy = findViewById(R.id.checkBoxPrivacy);
    Button buttonSignUp = findViewById(R.id.buttonSignUp);
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 로그인 버튼 클릭 시
        // 비밀번호 찾기 버튼 클릭 시
        /* sign_in.xml에서 회원가입 버튼 클릭시
        1. 필수 약관이 체크되었을 때만 버튼을 활성화
        checkBoxTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            buttonSignUp.setEnabled(checkBoxTerms.isChecked() && checkBoxPrivacy.isChecked());
        });
        checkBoxPrivacy.setOnCheckedChangeListener((buttonView, isChecked) -> {
            buttonSignUp.setEnabled(checkBoxTerms.isChecked() && checkBoxPrivacy.isChecked());
        });
        2. 이메일 인증번호 받기 버튼 클릭 시
        buttonSignUp.setOnClickListener(v -> {
            if (checkBoxTerms.isChecked() && checkBoxPrivacy.isChecked()) {
                // 회원가입 절차 실행
                Toast.makeText(this, "인증 번호를 발송하였습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "필수 약관에 동의해야 합니다.", Toast.LENGTH_SHORT).show();
            }
        });
        */

        fab = findViewById(R.id.fab);
        fabMain = findViewById(R.id.fab_main);
        cardOption1 = findViewById(R.id.card_option_1);
        cardOption2 = findViewById(R.id.card_option_2);

        fabMain.hide(); // 초기에는 플로팅 버튼 숨기기
        hideCardOptions(); // 카드 뷰 초기 숨기기

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, WriteActivity.class);
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
                fab.show();
                hideCardOptions(); // 카드 뷰 숨기기
                fabMain.hide(); // nav_home 선택 시 플로팅 버튼 숨기기
            } else if (id == R.id.nav_team) {
                selectedFragment = new TeamFragment(); // TeamFragment 추가
                fab.hide(); // 기본 FAB 숨기기
                showFabMain(); // 플로팅 버튼 보이기
                toggleCardOptions(); // 카드 뷰 보이기
            } else if (id == R.id.nav_chat) {
                selectedFragment = new ChatFragment(); // ChatFragment 추가
                fab.hide();
                hideCardOptions(); // 카드 뷰 숨기기
                fabMain.hide(); // nav_chat 선택 시 플로팅 버튼 숨기기
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment(); // ProfileFragment 추가
                fab.hide();
                hideCardOptions(); // 카드 뷰 숨기기
                fabMain.hide(); // nav_profile 선택 시 플로팅 버튼 숨기기
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });


        fabMain.setOnClickListener(view -> {
            hideFabMain(); // fab_main 클릭 시 플로팅 버튼 숨기기
        });

        // 카드 옵션 클릭 리스너 설정
        cardOption1.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CreateTeamActivity.class);
            startActivity(intent); // CreateTeamActivity 열기
        });

        cardOption2.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SearchTeamActivity.class);
            startActivity(intent); // SearchTeamActivity 열기
        });
    }


    private void showFabMain() {
        fabMain.show();
        isFabMainVisible = true;
    }

    private void hideFabMain() {
        fabMain.hide();
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