package com.example.kickunity.Profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.widget.EditText;

import com.example.kickunity.Auth.AuthApiService;
import com.example.kickunity.Auth.CheckResponse;
import com.example.kickunity.Auth.LoginActivity;
import com.example.kickunity.R;
import com.example.kickunity.Auth.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private TextView textProfileName, textTeamInfo, textEmail, textBirthdate;
    private Button editProfileButton, logoutButton, deleteAccountButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // UI 요소 초기화
        initializeUI(view);

        // SharedPreferences에서 사용자 이메일과 토큰 가져오기
        String userEmail = getUserEmailFromSharedPreferences();
        String accessToken = getAccessTokenFromSharedPreferences();

        if (userEmail != null && accessToken != null) {
            loadUserProfile(userEmail, accessToken); // 사용자 프로필 정보 로드
        } else {
            Log.e(TAG, "사용자 이메일 또는 액세스 토큰이 없습니다.");
        }

        // 버튼 클릭 리스너 설정
        setupButtons();
        return view;
    }

    // UI 요소 초기화
    private void initializeUI(View view) {
        textProfileName = view.findViewById(R.id.TextprofileName);
        textTeamInfo = view.findViewById(R.id.TextTeamInfo);
        textEmail = view.findViewById(R.id.Textemail);
        textBirthdate = view.findViewById(R.id.TextBirthdate);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        deleteAccountButton = view.findViewById(R.id.deleteAccountButton);
    }

    // 버튼 클릭 리스너 설정
    private void setupButtons() {
        editProfileButton.setOnClickListener(v -> openProfileEditActivity());
        logoutButton.setOnClickListener(v -> showLogoutConfirmationDialog());
        deleteAccountButton.setOnClickListener(v -> showPasswordDialog());
    }

    // 프로필 수정 화면 열기
    private void openProfileEditActivity() {
        Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
        intent.putExtra("userEmail", textEmail.getText().toString());
        intent.putExtra("userName", textProfileName.getText().toString());
        startActivity(intent);
    }

    // 로그아웃 확인 다이얼로그 보여주기
    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("로그아웃")
                .setMessage("정말 로그아웃 하시겠습니까?")
                .setPositiveButton("확인", (dialog, which) -> handleLogout())
                .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // 로그아웃 처리
    private void handleLogout() {
        String accessToken = getAccessTokenFromSharedPreferences();

        if (accessToken != null) {
            logout(accessToken);
        } else {
            Log.e(TAG, "액세스 토큰이 없습니다.");
            redirectToLoginScreen();
        }
    }

    private void logout(String accessToken) {
        // AuthApiService 인스턴스 생성
        AuthApiService authApiService = RetrofitClient.getAuthApiService();

        // 로그아웃 요청 보내기
        Call<Void> call = authApiService.logout("Bearer " + accessToken);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // 로그아웃 성공 처리
                    clearSharedPreferences();
                    redirectToLoginScreen();
                } else {
                    // 실패 응답 처리 (예: 500 서버 오류)
                    Log.e(TAG, "로그아웃 실패. 응답 코드: " + response.code());
                    Toast.makeText(getActivity(), "로그아웃 실패: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // 네트워크 오류나 기타 실패 처리
                Log.e(TAG, "로그아웃 요청 실패: " + t.getMessage());
                Toast.makeText(getActivity(), "로그아웃 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // 계정 삭제를 위한 비밀번호 입력 다이얼로그
    private void showPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("비밀번호 입력");

        EditText input = new EditText(getContext());
        builder.setView(input);

        builder.setPositiveButton("확인", (dialog, which) -> {
            String password = input.getText().toString().trim();
            if (!password.isEmpty()) {
                deleteAccount(getAccessTokenFromSharedPreferences(), password);
            } else {
                Toast.makeText(getActivity(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // 계정 삭제 요청
    private void deleteAccount(String token, String password) {
        // 비밀번호를 포함한 요청 객체 생성
        DeleteMemberRequest request = new DeleteMemberRequest(password);

        // Retrofit 인스턴스 사용
        AuthApiService authApiService = RetrofitClient.getRetrofitInstance().create(AuthApiService.class);

        // 계정 삭제 요청
        Call<CheckResponse> call = authApiService.deleteAccount("Bearer " + token, request);
        call.enqueue(new Callback<CheckResponse>() {
            @Override
            public void onResponse(Call<CheckResponse> call, Response<CheckResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    clearSharedPreferences();
                    redirectToLoginScreen();
                } else {
                    Toast.makeText(getContext(), "계정 삭제 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CheckResponse> call, Throwable t) {
                Toast.makeText(getContext(), "에러 발생: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // SharedPreferences에서 사용자 정보 삭제
    private void clearSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("auth", getContext().MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    // 로그인 화면으로 리다이렉트
    private void redirectToLoginScreen() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    // 사용자 프로필 정보 로드
    private void loadUserProfile(String userEmail, String accessToken) {
        AuthApiService authApiService = RetrofitClient.getRetrofitInstance().create(AuthApiService.class);
        Call<MypageResponse> call = authApiService.getMyPageInfo("Bearer " + accessToken, userEmail);

        call.enqueue(new Callback<MypageResponse>() {
            @Override
            public void onResponse(Call<MypageResponse> call, Response<MypageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                } else {
                    Log.e(TAG, "프로필 로드 실패: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<MypageResponse> call, Throwable t) {
                Log.e(TAG, "프로필 요청 실패: " + t.getMessage());
            }
        });
    }

    // UI 업데이트
    private void updateUI(MypageResponse response) {
        textProfileName.setText(response.getName() != null ? response.getName() : "");
        textTeamInfo.setText(response.getTeam() != null ? response.getTeam() : "");
        textEmail.setText(response.getEmail() != null ? response.getEmail() : "");
        textBirthdate.setText(response.getBirth() != null ? response.getBirth() : "");
    }

    // SharedPreferences에서 이메일 가져오기
    private String getUserEmailFromSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("auth", getContext().MODE_PRIVATE);
        return sharedPreferences.getString("userEmail", null);
    }

    // SharedPreferences에서 액세스 토큰 가져오기
    private String getAccessTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("auth", getContext().MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", null);
    }
}
