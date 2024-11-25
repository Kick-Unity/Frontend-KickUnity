package com.example.kickunity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseballBoardFragment extends Fragment {

    private static final String TAG = "BaseballBoardFragment";
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_baseballboard, container, false);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        setupRecyclerView(view);
        setupBackButton(view);
        setupFloatingActionButton();

        // "BASEBALL" 카테고리의 게시글 목록 가져오기
        loadBoardsByCategory("BASEBALL");

        return view;
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(new ArrayList<>(), post -> openPostDetailFragment(post));
        recyclerView.setAdapter(postAdapter);
    }

    private void setupBackButton(View view) {
        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            FloatingActionButton fabCreatePost = getActivity().findViewById(R.id.fabCreatePost);
            if (fabCreatePost != null) {
                fabCreatePost.hide();
            }
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private void setupFloatingActionButton() {
        FloatingActionButton fabCreatePost = getActivity().findViewById(R.id.fabCreatePost);
        if (fabCreatePost != null) {
            fabCreatePost.show();
            fabCreatePost.setOnClickListener(v -> startActivity(new Intent(getContext(), WriteActivity.class)));
        }
    }

    private void openPostDetailFragment(BoardSummaryResponse post) {
        Bundle args = new Bundle();
        args.putString("title", post.getTitle());
        args.putString("content", post.getContent());

        Fragment postDetailFragment = new PostDetailFragment();
        postDetailFragment.setArguments(args);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, postDetailFragment)
                .addToBackStack(null)
                .commit();
    }

    private void loadBoardsByCategory(String category) {
        Call<List<BoardSummaryResponse>> call = apiService.getBoardsByCategory(category);

        call.enqueue(new Callback<List<BoardSummaryResponse>>() {
            @Override
            public void onResponse(Call<List<BoardSummaryResponse>> call, Response<List<BoardSummaryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BoardSummaryResponse> boards = response.body();

                    // 게시글 리스트를 ID 기준 내림차순으로 정렬
                    sortBoardsByIdDesc(boards);

                    // 어댑터에 정렬된 게시글 목록 전달
                    postAdapter.updatePosts(boards);
                    Log.d(TAG, "게시글 조회 성공: " + boards.size() + "개");
                } else {
                    handleError(response);
                }
            }

            @Override
            public void onFailure(Call<List<BoardSummaryResponse>> call, Throwable t) {
                Log.e(TAG, "네트워크 요청 실패", t);
                showToast("네트워크 오류가 발생했습니다.");
            }
        });
    }

    // ID 기준 내림차순 정렬 메서드
    private void sortBoardsByIdDesc(List<BoardSummaryResponse> boards) {
        // 게시글 ID가 높은 순서대로 정렬 (내림차순)
        boards.sort((board1, board2) -> Long.compare(board2.getId(), board1.getId()));
    }

    private void handleError(Response<?> response) {
        String errorMessage;
        try {
            errorMessage = response.errorBody() != null ? response.errorBody().string() : "Unknown error occurred";
        } catch (Exception e) {
            errorMessage = "Error reading error body: " + e.getMessage();
        }
        Log.e(TAG, "Error code: " + response.code() + ", message: " + errorMessage);
        showToast("게시글이 존재하지 않습니다.");
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
