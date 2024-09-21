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

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    private FloatingActionButton fab;
    private static final int WRITE_REQUEST_CODE = 1;
    private PostViewModel postViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.fab);
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
            } else if (id == R.id.nav_team) {
                selectedFragment = new TeamFragment(); // TeamFragment 추가
                fab.hide();
            } else if (id == R.id.nav_chat) {
                selectedFragment = new ChatFragment(); // ChatFragment 추가
                fab.hide();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment(); // ProfileFragment 추가
                fab.hide();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });
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
