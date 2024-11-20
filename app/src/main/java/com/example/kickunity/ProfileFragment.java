package com.example.kickunity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private EditText editTextProfileName, editTextTeamInfo, editTextEmail, editTextBirthdate;
    private Button editProfileButton, logoutButton, deleteAccountButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // EditText 필드를 초기화합니다
        editTextProfileName = view.findViewById(R.id.editTextprofileName);
        editTextTeamInfo = view.findViewById(R.id.editTextteamInfo);
        editTextEmail = view.findViewById(R.id.editTextemail);
        editTextBirthdate = view.findViewById(R.id.editTextbirthdate);

        // 버튼 초기화
        editProfileButton = view.findViewById(R.id.editProfileButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        deleteAccountButton = view.findViewById(R.id.deleteAccountButton);

        // 로그인된 사용자의 이메일을 SharedPreferences에서 읽어오기
        String userEmail = getUserEmailFromSharedPreferences();
        String accessToken = getAccessTokenFromSharedPreferences();  // 액세스 토큰을 가져옵니다.

        if (userEmail != null && accessToken != null) {
            loadUserProfile(userEmail, accessToken);  // 이메일과 액세스 토큰을 함께 보내기
        } else {
            Log.e(TAG, "사용자 이메일 또는 액세스 토큰이 저장되지 않았습니다.");
        }

        setupEditProfileButton();

        return view;
    }
    private void setupEditProfileButton() {
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
            startActivity(intent);
        });
    }

    // SharedPreferences에서 사용자 이메일 가져오기
    private String getUserEmailFromSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("auth", getContext().MODE_PRIVATE);
        return sharedPreferences.getString("userEmail", null);  // 저장된 이메일 값 반환, 없으면 null 반환
    }

    // SharedPreferences에서 액세스 토큰 가져오기
    private String getAccessTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("auth", getContext().MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", null);  // 저장된 accessToken 반환, 없으면 null 반환
    }

    // 사용자 정보 로드 (GET 요청)
    private void loadUserProfile(String userEmail, String accessToken) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        // 이메일을 기반으로 사용자 정보를 요청
        Call<MypageResponse> call = apiService.getMyPageInfo("Bearer " + accessToken, userEmail);  // Authorization 헤더 추가

        call.enqueue(new Callback<MypageResponse>() {
            @Override
            public void onResponse(Call<MypageResponse> call, Response<MypageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MypageResponse mypageResponse = response.body();

                    // 가져온 프로필 정보를 각 EditText에 설정
                    if (mypageResponse.getName() != null) {
                        editTextProfileName.setText(mypageResponse.getName());
                    }
                    if (mypageResponse.getTeam() != null) {
                        editTextTeamInfo.setText(mypageResponse.getTeam());
                    }
                    if (mypageResponse.getEmail() != null) {
                        editTextEmail.setText(mypageResponse.getEmail());
                    }
                    if (mypageResponse.getBirth() != null) {
                        editTextBirthdate.setText(mypageResponse.getBirth());
                    }

                } else {
                    Log.e(TAG, "사용자 프로필 로드 실패, 응답 코드: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<MypageResponse> call, Throwable t) {
                Log.e(TAG, "API 호출 실패: " + t.getMessage());
            }
        });
    }



}