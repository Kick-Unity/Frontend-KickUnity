package com.example.kickunity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<BoardSummaryResponse> boards; // Board 객체 리스트
    private final OnPostClickListener onPostClickListener;

    // Constructor
    public PostAdapter(List<BoardSummaryResponse> boards, OnPostClickListener onPostClickListener) {
        this.boards = boards != null ? boards : new ArrayList<>();
        this.onPostClickListener = onPostClickListener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        BoardSummaryResponse board = boards.get(position);
        holder.bind(board);
    }

    @Override
    public int getItemCount() {
        return boards.size();
    }

    // 게시글 목록 업데이트 메서드
    public void updatePosts(List<BoardSummaryResponse> newBoards) {
        this.boards.clear();
        this.boards.addAll(newBoards);
        notifyDataSetChanged();
    }

    // 게시글 클릭 시 호출될 리스너 인터페이스
    public interface OnPostClickListener {
        void onPostClick(BoardSummaryResponse board);
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView dateTextView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.postTitle);
            dateTextView = itemView.findViewById(R.id.date);

            // 클릭 이벤트 처리
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onPostClickListener.onPostClick(boards.get(position));
                }
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bind(BoardSummaryResponse board) {
            titleTextView.setText(board.getTitle() != null ? board.getTitle() : "Untitled");

            String dateString = board.getcreatedDate();  // 서버에서 받은 날짜 문자열, 예: "2024-11-22 06:50:45"

            // 날짜 포맷 설정
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM/dd HH:mm"); // 출력 형식

            try {
                // 문자열을 ZonedDateTime으로 변환 (UTC로 해석)
                String dateWithTimeZone = dateString.replace(" ", "T") + "Z"; // 공백을 'T'로 변경하고, 'Z' 추가하여 UTC 형식으로 만듦
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateWithTimeZone, DateTimeFormatter.ISO_DATE_TIME);

                // 한국 표준시로 변환
                ZonedDateTime koreaTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));

                // 원하는 형식으로 변환
                String formattedDate = koreaTime.format(outputFormatter);

                // 변환된 날짜를 TextView에 설정
                if (dateTextView != null) {
                    dateTextView.setText(formattedDate);
                }
            } catch (Exception e) {
                // 에러 로그 추가
                Log.e("PostAdapter", "Failed to parse date: " + dateString, e);

                // 날짜 파싱 실패 시 "Invalid date" 출력
                if (dateTextView != null) {
                    dateTextView.setText("Invalid date");
                }
            }
        }
    }
}
