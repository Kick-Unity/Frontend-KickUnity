package com.example.kickunity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PostDetailFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        // 뒤로 가기 버튼 설정
        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Fragment 종료
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // 전달된 게시글 정보 받기
        Bundle args = getArguments();
        if (args != null) {
            String title = args.getString("title");
            String content = args.getString("content");
            String category = args.getString("category");
            String time = args.getString("time");
            String nickname = args.getString("nickname");  // 닉네임
            String date = args.getString("date");  // 날짜

            // 게시글 내용을 보여줄 TextView들 설정
            TextView titleTextView = view.findViewById(R.id.postTitle);
            TextView contentTextView = view.findViewById(R.id.postContent);
            TextView categoryTextView = view.findViewById(R.id.postCategory);
            TextView nicknameTextView = view.findViewById(R.id.postdetail_nickname);
            TextView dateTextView = view.findViewById(R.id.postdetail_date);

            // 전달받은 값들 설정
            titleTextView.setText(title);
            contentTextView.setText(content);
            categoryTextView.setText(category);
            nicknameTextView.setText(nickname);  // 닉네임 설정
            dateTextView.setText(date);  // 날짜 설정

            // fab 숨기기
            FloatingActionButton fabCreatePost = getActivity().findViewById(R.id.fabCreatePost);
            fabCreatePost.hide();
        }

        return view;
    }
}
