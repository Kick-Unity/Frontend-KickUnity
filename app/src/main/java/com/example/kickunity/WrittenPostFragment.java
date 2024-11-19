package com.example.kickunity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class WrittenPostFragment extends Fragment {

    private PostViewModel postViewModel;
    private LinearLayout writtenpost_scrollView;
    private String currentUserEmail; // 현재 사용자의 이메일을 저장할 변수

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_writtenpost, container, false);
        writtenpost_scrollView = view.findViewById(R.id.writtenpost_scrollView);

        // 현재 사용자 이메일 가져오기 (예시: SharedPreferences에서 가져오는 방식)
        currentUserEmail = getCurrentUserEmail();

        // 뒤로 가기 버튼 설정
        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Fragment 종료
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        postViewModel = new ViewModelProvider(requireActivity()).get(PostViewModel.class);
        postViewModel.getPosts().observe(getViewLifecycleOwner(), posts -> {
            writtenpost_scrollView.removeAllViews(); // 기존 게시글 제거
            for (PostViewModel.Post post : posts) {
                addPostView(post.title, post.content, post.category, post.time);
            }
        });

        return view;
    }

    // 현재 사용자 이메일을 반환하는 메서드 (예시: SharedPreferences에서 가져오기)
    private String getCurrentUserEmail() {
        // 여기서 실제로 이메일을 가져오는 방법을 구현하세요.
        // 예시로 SharedPreferences에서 가져오는 방식 사용
        SharedPreferences prefs = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        return prefs.getString("user_email", ""); // "user_email" 키에 저장된 이메일 반환
    }

    private void addPostView(String title, String content, String category, String time) {
        LinearLayout postLayout = new LinearLayout(getContext());
        postLayout.setOrientation(LinearLayout.VERTICAL);
        postLayout.setPadding(32, 32, 32, 32);
        postLayout.setBackgroundResource(R.drawable.post_background);
        postLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        postLayout.setElevation(4);

        LinearLayout timeTitleLayout = new LinearLayout(getContext());
        timeTitleLayout.setOrientation(LinearLayout.HORIZONTAL);
        timeTitleLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView postTime = new TextView(getContext());
        postTime.setText(time);
        postTime.setTextColor(Color.parseColor("#00ADB5"));
        postTime.setBackgroundColor(Color.TRANSPARENT);
        postTime.setPadding(8, 0, 30, 0);
        postTime.setTextSize(18);
        postTime.setTypeface(null, Typeface.BOLD);

        TextView postTitle = new TextView(getContext());
        postTitle.setText(title);
        postTitle.setTextSize(18);
        postTitle.setTextColor(Color.BLACK);
        postTitle.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        // 시간과 제목을 수평으로 나란히 배치
        timeTitleLayout.addView(postTime);
        timeTitleLayout.addView(postTitle);

        // postLayout에 시간과 제목 레이아웃 추가
        postLayout.addView(timeTitleLayout);

        // 터치 이벤트 추가
        postLayout.setOnClickListener(v -> {
            // PostDetailFragment로 이동하면서 해당 게시글의 정보를 전달
            Fragment postDetailFragment = new PostDetailFragment();
            Bundle args = new Bundle();
            args.putString("title", title);
            args.putString("content", content);
            args.putString("category", category);
            args.putString("time", time);
            postDetailFragment.setArguments(args);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, postDetailFragment) // frame_container는 PostDetailFragment가 들어갈 컨테이너의 ID입니다.
                    .addToBackStack(null)
                    .commit();
        });

        // 구분선 추가
        View divider = new View(getContext());
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 2);
        dividerParams.setMargins(0, 25, 0, 25);
        divider.setLayoutParams(dividerParams);
        divider.setBackgroundColor(Color.LTGRAY);

        writtenpost_scrollView.addView(postLayout);
        writtenpost_scrollView.addView(divider);
    }
}
