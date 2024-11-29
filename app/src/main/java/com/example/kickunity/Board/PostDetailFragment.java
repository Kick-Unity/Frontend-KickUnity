package com.example.kickunity.Board;

import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.kickunity.R;
import com.example.kickunity.Auth.RetrofitClient; // RetrofitClient 임포트
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

        FloatingActionButton fabCreatePost = getActivity().findViewById(R.id.fabCreatePost);
        fabCreatePost.hide();

        // 전달된 게시글 ID 받기
        Bundle args = getArguments();
        if (args != null) {
            Long postId = args.getLong("postId");  // 게시글 ID
            Log.d("PostDetailFragment", "Received post ID: " + postId);  // 게시글 ID 로그 출력

            // 서버로부터 게시글 상세 정보 가져오기
            fetchPostDetails(postId);

            // More 버튼 클릭 시 팝업 메뉴 설정
            ImageButton moreButton = view.findViewById(R.id.moreButton);
            moreButton.setOnClickListener(v -> {
                // 팝업 메뉴 생성
                PopupMenu popupMenu = new PopupMenu(getContext(), v);
                getActivity().getMenuInflater().inflate(R.menu.menu_post_options, popupMenu.getMenu());

                // 메뉴 항목 클릭 시의 동작 처리
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.menu_edit) {
                        if (postId != null) { // postId가 null이 아닌지 확인
                            Intent intent = new Intent(requireContext(), BoardEditActivity.class);
                            intent.putExtra("boardId", postId); // 게시글 ID 전달
                            startActivity(intent);
                        } else {
                            Log.e("PostDetailFragment", "postId is null, cannot start EditActivity");
                            Toast.makeText(requireContext(), "게시글 ID를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    } else if (item.getItemId() == R.id.menu_delete) {
                        // 삭제 버튼 클릭 시
                        if (postId != null) {
                            confirmDeletePost(postId); // 게시글 삭제 확인 다이얼로그 호출
                        }
                        return true;
                    } else {
                        return false;
                    }
                });

                // 메뉴 표시
                popupMenu.show();
            });
        }
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

    // 게시글 삭제 확인 다이얼로그
    private void confirmDeletePost(Long postId) {
        new AlertDialog.Builder(getContext())
                .setTitle("게시글 삭제")
                .setMessage("정말로 게시글을 삭제하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePost(postId); // 사용자가 확인을 눌렀을 때 삭제 실행
                    }
                })
                .setNegativeButton("취소", null) // 취소 버튼 클릭 시 아무 작업 하지 않음
                .show();
    }

    // 게시글 삭제 메서드
    private void deletePost(Long postId) {
        String accessToken = getActivity().getSharedPreferences("auth", getContext().MODE_PRIVATE).getString("accessToken", null);

        if (accessToken == null) {
            Toast.makeText(getContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String authorizationHeader = "Bearer " + accessToken;

        // 서버에 게시글 삭제 요청
        boardApiService.deleteBoard(authorizationHeader, postId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack(); // 삭제 후 이전 화면으로 돌아가기
                } else {
                    Toast.makeText(getContext(), "게시글 삭제 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
