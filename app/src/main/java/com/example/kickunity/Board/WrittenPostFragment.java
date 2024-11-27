package com.example.kickunity.Board;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kickunity.Auth.AuthApiService;
import com.example.kickunity.R;
import com.example.kickunity.Auth.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WrittenPostFragment extends Fragment {

    private static final String TAG = "WrittenPostFragment";
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private BoardApiService boardApiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_writtenpost, container, false);

        // Retrofit을 통해 BoardApiService 초기화
        boardApiService = RetrofitClient.getBoardApiService();

        // RecyclerView 및 UI 설정
        setupRecyclerView(view);
        setupBackButton(view);
        setupFloatingActionButton();

        // 사용자 게시글 목록 로드
        loadMyBoards();

        return view;
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(new ArrayList<>(), this::openPostDetailFragment);
        recyclerView.setAdapter(postAdapter);
    }

    private void setupBackButton(View view) {
        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
    }

    private void setupFloatingActionButton() {
        FloatingActionButton fabCreatePost = requireActivity().findViewById(R.id.fabCreatePost);
        if (fabCreatePost != null) {
            fabCreatePost.show();
            fabCreatePost.setOnClickListener(v -> startActivity(new Intent(getContext(), WriteActivity.class)));
        }
    }

    private void loadMyBoards() {
        String accessToken = getAccessTokenFromSharedPreferences();

        if (accessToken == null) {
            showToast("로그인이 필요합니다.");
            return;
        }

        String authorizationHeader = "Bearer " + accessToken;

        boardApiService.getMyBoards(authorizationHeader).enqueue(new Callback<List<BoardSummaryResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<BoardSummaryResponse>> call, @NonNull Response<List<BoardSummaryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BoardSummaryResponse> boards = response.body();
                    postAdapter.updatePosts(boards); // RecyclerView 데이터 갱신
                    Log.d(TAG, "내가 쓴 게시글 조회 성공: " + boards.size() + "개");
                } else {
                    handleError(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<BoardSummaryResponse>> call, @NonNull Throwable t) {
                Log.e(TAG, "내가 쓴 게시글 요청 실패", t);
                showToast("네트워크 오류가 발생했습니다.");
            }
        });
    }

    private String getAccessTokenFromSharedPreferences() {
        if (getContext() == null) return null;
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("auth", getContext().MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", null);
    }

    private void handleError(Response<?> response) {
        String errorMessage = "알 수 없는 오류가 발생했습니다.";
        if (response.errorBody() != null) {
            try {
                errorMessage = response.errorBody().string();
            } catch (Exception e) {
                Log.e(TAG, "Error reading error body: " + e.getMessage(), e);
            }
        }
        Log.e(TAG, "Error: " + errorMessage);
        showToast("게시글 조회 중 오류가 발생했습니다.");
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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
}
