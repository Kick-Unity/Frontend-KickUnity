package com.example.kickunity.Profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.widget.EditText;

import com.example.kickunity.Auth.AuthApiService;
import com.example.kickunity.Auth.CheckResponse;
import com.example.kickunity.Auth.LoginActivity;
import com.example.kickunity.Auth.LogoutHelper;
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

    // 로그아웃 확인 다이얼로그
    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("로그아웃")
                .setMessage("정말 로그아웃 하시겠습니까?")
                .setPositiveButton("확인", (dialog, which) -> handleLogout())
                .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                .setCancelable(false) // 다이얼로그 외부 터치 시 닫히지 않도록 설정
                .show();
    }

    // 로그아웃 처리
    private void handleLogout() {
        // SharedPreferences에서 refresh 토큰을 가져옵니다.
        String refreshToken = getRefreshTokenFromSharedPreferences();
        if (refreshToken != null) {
            // 쿠키에 refresh 토큰을 추가하여 로그아웃 요청
            String cookieHeader = "refresh=" + refreshToken; // refresh 토큰을 쿠키 형식으로 추가
            LogoutHelper.logout(getActivity(), cookieHeader);  // 로그아웃 헬퍼 메소드 호출

            // 로그아웃 후 로그인 화면으로 리다이렉트
            redirectToLoginScreen();
        } else {
            Toast.makeText(getContext(), "로그아웃 실패: refresh 토큰을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 로그인 화면으로 리다이렉트
    private void redirectToLoginScreen() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();  // 현재 Activity 종료
    }

    // 비밀번호 입력 다이얼로그
    private void showPasswordDialog() {
        // Fragment에서 getContext()를 사용하여 Context 얻기
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // 다이얼로그 제목 설정
        builder.setTitle("비밀번호 입력")
                .setMessage("계정을 삭제하려면 비밀번호를 입력하세요.");

        // 비밀번호 입력을 위한 EditText 생성
        final EditText input = new EditText(requireContext());
        input.setHint("비밀번호를 입력하세요");
        input.setHintTextColor(Color.parseColor("#757575")); // 회색 (#757575)
        input.setTextColor(Color.BLACK); // 텍스트 색상 설정 (검정색)
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD); // 비밀번호 입력 필드
        input.setPadding(40, 30, 40, 30); // 내부 여백 추가
        input.setBackgroundResource(R.drawable.edittext_background); // 배경을 설정 (뒤에서 정의한 drawable)

        // 레이아웃 설정
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 0, 50, 20); // 다이얼로그의 양옆 여백 설정
        layout.addView(input);

        // 다이얼로그에 레이아웃 적용
        builder.setView(layout);

        // "확인" 버튼 클릭 리스너
        builder.setPositiveButton("확인", (dialog, which) -> {
            String password = input.getText().toString().trim();
            if (!password.isEmpty()) {
                deleteAccount(getAccessTokenFromSharedPreferences(), password); // 계정 삭제 함수 호출
            } else {
                Toast.makeText(requireContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // "취소" 버튼 클릭 리스너
        builder.setNegativeButton("취소", (dialog, which) -> dialog.cancel());

        // 다이얼로그 생성
        AlertDialog dialog = builder.create();
        dialog.show();

        // 버튼 스타일 설정
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.parseColor("#FF0000")); // 삭제 버튼 텍스트 색상 변경 (빨간색)

        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.parseColor("#FFFFFF")); // 취소 버튼 텍스트 색상 변경 (검정색)
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

    // SharedPreferences에서 리프레시 토큰 가져오기
    private String getRefreshTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("auth", getContext().MODE_PRIVATE);
        return sharedPreferences.getString("refreshToken", null);
    }
}
