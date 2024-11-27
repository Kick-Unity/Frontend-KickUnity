package com.example.kickunity.Board;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.kickunity.R;
import com.example.kickunity.Auth.RetrofitClient; // RetrofitClient 임포트

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log; // Log 임포트

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class PostDetailFragment extends Fragment {

    private BoardApiService boardApiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        // RetrofitClient를 사용하여 BoardApiService 인스턴스 초기화
        boardApiService = RetrofitClient.getBoardApiService();

        // 뒤로 가기 버튼 설정
        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // 전달된 게시글 ID 받기
        Bundle args = getArguments();
        if (args != null) {
            Long postId = args.getLong("postId");  // 게시글 ID
            Log.d("PostDetailFragment", "Received post ID: " + postId);  // 게시글 ID 로그 출력

            // 서버로부터 게시글 상세 정보 가져오기
            fetchPostDetails(postId);
        }

        // More 버튼 클릭 시 팝업 메뉴 설정
        ImageButton moreButton = view.findViewById(R.id.moreButton);
        moreButton.setOnClickListener(v -> {
            // 팝업 메뉴 생성
            PopupMenu popupMenu = new PopupMenu(getContext(), v);
            getActivity().getMenuInflater().inflate(R.menu.menu_post_options, popupMenu.getMenu());

            // 메뉴 항목 클릭 시의 동작 처리
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_edit) {
                    // 수정 버튼 클릭 시

                    return true;
                } else if (item.getItemId() == R.id.menu_delete) {
                    // 삭제 버튼 클릭 시
                    // onDeletePost();
                    return true;
                } else {
                    return false;
                }
            });

            // 메뉴 표시
            popupMenu.show();
        });

        return view;
    }

    // 서버에서 게시글 상세 정보 가져오기
    private void fetchPostDetails(Long postId) {
        if (boardApiService == null) {
            Toast.makeText(getContext(), "API 서비스가 초기화되지 않았습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 서버에서 게시글 상세 정보 가져오기
        boardApiService.getBoardDetail(postId).enqueue(new Callback<BoardDetailResponse>() {
            @Override
            public void onResponse(Call<BoardDetailResponse> call, Response<BoardDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BoardDetailResponse boardDetail = response.body();
                    updateUIWithPostDetails(boardDetail);
                } else {
                    Toast.makeText(getContext(), "게시글 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BoardDetailResponse> call, Throwable t) {
                Toast.makeText(getContext(), "서버 요청 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // UI를 서버로부터 받은 게시글 상세 정보로 업데이트
    // UI를 서버로부터 받은 게시글 상세 정보로 업데이트
    private void updateUIWithPostDetails(BoardDetailResponse boardDetail) {
        View view = getView();
        if (view != null) {
            // UI 요소들을 초기화
            TextView titleTextView = view.findViewById(R.id.postTitle);
            TextView contentTextView = view.findViewById(R.id.postContent);
            TextView nicknameTextView = view.findViewById(R.id.postdetail_nickname);
            TextView dateTextView = view.findViewById(R.id.postdetail_date);

            // 서버에서 받은 데이터로 UI 업데이트
            if (boardDetail.getAuthorName() != null) {
                nicknameTextView.setText(boardDetail.getAuthorName());  // 닉네임
            }

            if (boardDetail.getCreatedDate() != null) {
                // 날짜 포맷팅
                String formattedDate = formatDateToKST(boardDetail.getCreatedDate());
                dateTextView.setText(formattedDate);  // 포맷된 날짜 설정
            }

            if (boardDetail.getTitle() != null) {
                titleTextView.setText(boardDetail.getTitle());  // 글 제목
            }

            if (boardDetail.getContent() != null) {
                contentTextView.setText(boardDetail.getContent());  // 글 내용
            }
        }
    }

    // 날짜를 KST로 변환하고 포맷팅하는 유틸리티 메서드
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String formatDateToKST(String dateString) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM/dd HH:mm");

        try {
            // UTC 형식으로 ZonedDateTime 생성
            String dateWithTimeZone = dateString.replace(" ", "T") + "Z";
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateWithTimeZone, DateTimeFormatter.ISO_DATE_TIME);

            // KST로 변환
            ZonedDateTime koreaTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));

            // 지정된 출력 형식으로 포맷팅
            return koreaTime.format(outputFormatter);
        } catch (Exception e) {
            // 에러 로그 출력
            Log.e("PostDetailFragment", "Failed to parse date: " + dateString, e);
            return "Invalid date";
        }
    }

}
