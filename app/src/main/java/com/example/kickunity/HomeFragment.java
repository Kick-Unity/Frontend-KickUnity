package com.example.kickunity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.widget.TextView;
import android.widget.Toast;

public class HomeFragment extends Fragment {

    private TextView noticeContent;
    private Button mainBoardButton, soccerBoardButton, basketballBoardButton, baseballBoardButton, otherSportsBoardButton, myPostContentButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 이 프래그먼트를 위한 레이아웃을 인플레이트
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 뷰 초기화
        noticeContent = view.findViewById(R.id.noticeContent);
        mainBoardButton = view.findViewById(R.id.mainboard);
        soccerBoardButton = view.findViewById(R.id.soccerBoard);
        basketballBoardButton = view.findViewById(R.id.basketballBoard);
        baseballBoardButton = view.findViewById(R.id.baseballBoard);
        otherSportsBoardButton = view.findViewById(R.id.otherSportsBoard);
        myPostContentButton = view.findViewById(R.id.myPostContent);

        // 공지사항 내용 설정
        noticeContent.setText("다음 주 토요일에 이벤트가 있습니다.");

        // 버튼 클릭 리스너 설정
        mainBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 전체 게시판 fragment_mainboard로 이동하는 코드
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                MainBoardFragment mainBoardFragment = new MainBoardFragment();
                transaction.replace(R.id.frame_container, mainBoardFragment); // fragment_container는 프래그먼트가 표시될 레이아웃 ID입니다.
                transaction.addToBackStack(null); // 뒤로 가기 버튼을 눌렀을 때 이전 프래그먼트로 돌아가기 위함
                transaction.commit();
            }

        });

        soccerBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 축구 게시판 fragment_soccerboard로 이동하는 코드
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                SoccerBoardFragment soccerBoardFragment = new SoccerBoardFragment();
                transaction.replace(R.id.frame_container, soccerBoardFragment); // fragment_container는 프래그먼트가 표시될 레이아웃 ID입니다.
                transaction.addToBackStack(null); // 뒤로 가기 버튼을 눌렀을 때 이전 프래그먼트로 돌아가기 위함
                transaction.commit();
            }
        });

        basketballBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 농구 게시판 fragment_basketballboard로 이동하는 코드
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                BasketballBoardFragment basketballBoardFragment = new BasketballBoardFragment();
                transaction.replace(R.id.frame_container, basketballBoardFragment); // fragment_container는 프래그먼트가 표시될 레이아웃 ID입니다.
                transaction.addToBackStack(null); // 뒤로 가기 버튼을 눌렀을 때 이전 프래그먼트로 돌아가기 위함
                transaction.commit();
            }
        });

        baseballBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 야구 게시판 fragment_baseballboard로 이동하는 코드
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                BaseballBoardFragment baseballBoardFragment = new BaseballBoardFragment();
                transaction.replace(R.id.frame_container, baseballBoardFragment); // fragment_container는 프래그먼트가 표시될 레이아웃 ID입니다.
                transaction.addToBackStack(null); // 뒤로 가기 버튼을 눌렀을 때 이전 프래그먼트로 돌아가기 위함
                transaction.commit();
            }
        });

        otherSportsBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 기타 종목 게시판 fragment_othersportsboard로 이동하는 코드
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                OtherSportsBoardFragment othersportsBoardFragment = new OtherSportsBoardFragment();
                transaction.replace(R.id.frame_container, othersportsBoardFragment); // fragment_container는 프래그먼트가 표시될 레이아웃 ID입니다.
                transaction.addToBackStack(null); // 뒤로 가기 버튼을 눌렀을 때 이전 프래그먼트로 돌아가기 위함
                transaction.commit();
            }
        });

        myPostContentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 내가 쓴 글로 이동하는 코드 추가
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                WrittenPostFragment writtenpostFragment = new WrittenPostFragment();
                transaction.replace(R.id.frame_container, writtenpostFragment); // fragment_container는 프래그먼트가 표시될 레이아웃 ID입니다.
                transaction.addToBackStack(null); // 뒤로 가기 버튼을 눌렀을 때 이전 프래그먼트로 돌아가기 위함
                transaction.commit();
            }
        });

        return view;
    }
}