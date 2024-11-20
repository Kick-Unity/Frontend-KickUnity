package com.example.kickunity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

    // ViewHolder 클래스
    class PostViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.postTitle);

            // 클릭 이벤트 처리
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onPostClickListener.onPostClick(boards.get(position));
                }
            });
        }

        // View에 데이터를 바인딩하는 메서드
        public void bind(BoardSummaryResponse board) {
            titleTextView.setText(board.getTitle() != null ? board.getTitle() : "Untitled");
        }
    }
}
