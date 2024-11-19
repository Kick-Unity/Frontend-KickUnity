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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainBoardFragment extends Fragment {

    private PostViewModel postViewModel;
    private LinearLayout home_scrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mainboard, container, false);
        home_scrollView = view.findViewById(R.id.home_scrollView);

        // 뒤로 가기 버튼 설정
        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // 플로팅 버튼 숨기기
            FloatingActionButton fabCreatePost = getActivity().findViewById(R.id.fabCreatePost);
            if (fabCreatePost != null) {
                fabCreatePost.hide();
            }
            // Fragment 종료
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        postViewModel = new ViewModelProvider(requireActivity()).get(PostViewModel.class);
        postViewModel.getPosts().observe(getViewLifecycleOwner(), posts -> {
            home_scrollView.removeAllViews(); // 기존 게시글 제거
            for (PostViewModel.Post post : posts) {
                addPostView(post.title, post.content, post.category, post.time); // 시간 정보 추가
            }
        });

        // fab 다시 보이기
        FloatingActionButton fabCreatePost = getActivity().findViewById(R.id.fabCreatePost);
        fabCreatePost.show();

        return view;
    }

    private void addPostView(String title, String content, String category, String time) {
        LinearLayout postLayout = new LinearLayout(getContext());
        postLayout.setOrientation(LinearLayout.VERTICAL);
        postLayout.setPadding(32, 32, 32, 32); // 패딩을 크게 설정하여 공간을 많이 차지하게 함
        postLayout.setBackgroundResource(R.drawable.post_background);
        postLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        postLayout.setElevation(4); // 그림자 효과 추가

        // 시간과 제목을 위한 레이아웃
        LinearLayout timeTitleLayout = new LinearLayout(getContext());
        timeTitleLayout.setOrientation(LinearLayout.HORIZONTAL);
        timeTitleLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // 시간 TextView
        TextView postTime = new TextView(getContext());
        postTime.setText(time); // 전달받은 시간 사용
        postTime.setTextColor(Color.parseColor("#00ADB5"));
        postTime.setBackgroundColor(Color.TRANSPARENT); // 배경을 투명하게
        postTime.setPadding(8, 0, 30, 0); // 여백 조정
        postTime.setTextSize(18); // 글자 크기 설정
        postTime.setTypeface(null, Typeface.BOLD);
        postTime.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // 제목 TextView
        TextView postTitle = new TextView(getContext());
        postTitle.setText(title);
        postTitle.setTextSize(18);
        postTitle.setTextColor(Color.BLACK);
        postTitle.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1)); // 제목이 나머지 공간을 차지하도록 설정

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
                LinearLayout.LayoutParams.MATCH_PARENT, 2); // 높이 2px의 구분선
        dividerParams.setMargins(0, 25, 0, 25); // 구분선의 위아래 여백을 더 크게 설정 (32dp)
        divider.setLayoutParams(dividerParams);
        divider.setBackgroundColor(Color.LTGRAY); // 구분선 색상 설정

        // postLayout을 postContainer에 추가
        home_scrollView.addView(postLayout);

        // 구분선 추가
        home_scrollView.addView(divider);
    }


}