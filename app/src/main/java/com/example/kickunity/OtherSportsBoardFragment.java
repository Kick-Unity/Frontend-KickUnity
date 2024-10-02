package com.example.kickunity;

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

public class OtherSportsBoardFragment extends Fragment {

    private PostViewModel postViewModel;
    private LinearLayout othersportsBoard_scrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_othersportsboard, container, false);
        othersportsBoard_scrollView = view.findViewById(R.id.othersportsBoard_scrollView);

        // 뒤로 가기 버튼 설정
        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Fragment 종료
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        postViewModel = new ViewModelProvider(requireActivity()).get(PostViewModel.class);
        postViewModel.getPosts().observe(getViewLifecycleOwner(), posts -> {
            othersportsBoard_scrollView.removeAllViews(); // 기존 게시글 제거
            for (PostViewModel.Post post : posts) {
                if (post.category.equals("기타 스포츠 게시판")) {  // 기타 스포츠 게시판 카테고리 필터
                    addPostView(post.title, post.content, post.category, post.time);
                }
            }
        });

        return view;
    }

    private void addPostView(String title, String content, String category, String time) {
        LinearLayout postLayout = new LinearLayout(getContext());
        postLayout.setOrientation(LinearLayout.VERTICAL);
        postLayout.setPadding(32, 32, 32, 32); // 패딩 설정
        postLayout.setBackgroundResource(R.drawable.post_background);
        postLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        postLayout.setElevation(4);

        // 시간과 제목을 위한 레이아웃
        LinearLayout timeTitleLayout = new LinearLayout(getContext());
        timeTitleLayout.setOrientation(LinearLayout.HORIZONTAL);
        timeTitleLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // 시간 TextView
        TextView postTime = new TextView(getContext());
        postTime.setText(time);
        postTime.setTextColor(Color.parseColor("#1867FF"));
        postTime.setBackgroundColor(Color.TRANSPARENT);
        postTime.setPadding(8, 0, 30, 0);
        postTime.setTextSize(18);
        postTime.setTypeface(null, Typeface.BOLD);

        // 제목 TextView
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

        // 구분선 추가
        View divider = new View(getContext());
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 2);
        dividerParams.setMargins(0, 25, 0, 25);
        divider.setLayoutParams(dividerParams);
        divider.setBackgroundColor(Color.LTGRAY);

        // postLayout을 home_scrollView에 추가
        othersportsBoard_scrollView.addView(postLayout);
        othersportsBoard_scrollView.addView(divider);
    }
}