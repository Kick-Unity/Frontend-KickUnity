package com.example.kickunity.Team;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.kickunity.Auth.HomeActivity;
import com.example.kickunity.Board.MainBoardFragment;
import com.example.kickunity.Profile.ProfileEditActivity;
import com.example.kickunity.R;
import com.example.kickunity.Team.TeamApiService;
import com.example.kickunity.Team.TeamDetailResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TeamFragment extends Fragment {

    private static final int PICK_IMAGE = 1;
    private TextView teamNameTextView;
    private TextView teamCategoryTextView;
    private TextView teamStartDateTextView;
    private TextView teamRegionTextView;
    private TextView teamAgeTextView;
    private TextView teamSizeTextView;
    private TextView teamDescriptionTextView;
    private Button teamSearchButton, editTeamButton;

    private Retrofit retrofit;
    private TeamApiService teamApiService;

    private SharedPreferences sharedPreferences;
    private Long teamId;  // teamId는 Long 타입으로 선언

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team, container, false);

        // View 연결
        teamNameTextView = view.findViewById(R.id.teamName);
        teamCategoryTextView = view.findViewById(R.id.teamCategoryTextView);
        teamStartDateTextView = view.findViewById(R.id.editTextFoundingYear);
        teamRegionTextView = view.findViewById(R.id.editTextActivityRegion);
        teamAgeTextView = view.findViewById(R.id.editTextAgeGroup);
        teamSizeTextView = view.findViewById(R.id.editTextTeamMembers);
        teamDescriptionTextView = view.findViewById(R.id.editTextClubDescription);
        teamSearchButton = view.findViewById(R.id.teamSearch);
        editTeamButton = view.findViewById(R.id.editTeamButton);

        // Fragment에서 전달된 teamId 받기
        Bundle arguments = getArguments();
        if (arguments != null) {
            teamId = arguments.getLong("teamId"); // "teamId" 값을 Long으로 받아옴
        }

        // 팀 ID 값 로그로 확인
        Log.d("TeamFragment", "팀 ID: " + teamId);

        // Retrofit 초기화 및 API 호출
        retrofit = new Retrofit.Builder()
                .baseUrl("http://15.165.133.129:8080/") // 서버 주소
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        teamApiService = retrofit.create(TeamApiService.class);

        String accessToken = getAccessTokenFromSharedPreferences();

        if (teamId != null && accessToken != null) {
            fetchTeamDetail(teamId, accessToken); // 팀 상세 정보를 가져오기
        } else {
            Log.e("TeamFragment", "팀 ID 또는 액세스 토큰이 없습니다.");
        }

        // 팀 검색 버튼 클릭 리스너에서 accessToken을 Intent로 넘깁니다.
        teamSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TeamSearchActivity로 이동하는 Intent 생성
                Intent intent = new Intent(getContext(), TeamSearchActivity.class);
                intent.putExtra("teamId", teamId); // 팀 ID를 추가로 전달
                intent.putExtra("accessToken", getAccessTokenFromSharedPreferences()); // accessToken을 추가로 전달
                startActivity(intent);  // TeamSearchActivity로 이동
            }
        });


        editTeamButton.setOnClickListener(v -> openTeamEditActivity());


        return view;
    }

    // 팀 수정 화면 열기
    private void openTeamEditActivity() {
        if (teamId != null) {
            // teamId를 TeamEditActivity로 넘김
            Intent intent = new Intent(getActivity(), TeamEditActivity.class);
            intent.putExtra("teamId", teamId); // teamId를 Long 타입으로 전달
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "팀 ID가 없습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
        }
    }


    private void fetchTeamDetail(Long teamId, String accessToken) {
        // Retrofit API 호출: teamId와 accessToken을 포함하여 요청
        Call<TeamDetailResponse> call = teamApiService.getTeamDetailWithToken(teamId, "Bearer " + accessToken);

        // API 호출 전에 로그로 teamId 확인
        Log.d("TeamFragment", "API 호출 전 teamId: " + teamId);

        call.enqueue(new Callback<TeamDetailResponse>() {
            @Override
            public void onResponse(Call<TeamDetailResponse> call, Response<TeamDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TeamDetailResponse teamDetail = response.body();
                    // UI에 팀 상세 정보를 설정
                    teamNameTextView.setText(teamDetail.getTeamName());
                    teamCategoryTextView.setText(teamDetail.getTeamCategory());
                    teamStartDateTextView.setText(teamDetail.getTeamStartDate());
                    teamRegionTextView.setText(teamDetail.getTeamRegion());
                    teamAgeTextView.setText(teamDetail.getTeamAge());
                    teamSizeTextView.setText(String.valueOf(teamDetail.getTeamSize()));
                    teamDescriptionTextView.setText(teamDetail.getTeamDescription());
                } else {
                    // 오류 처리 (상세한 오류 메시지)
                    Toast.makeText(getContext(), "팀 정보를 불러올 수 없습니다. 상태 코드: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TeamDetailResponse> call, Throwable t) {
                // 오류 처리 (네트워크 오류 시)
                Toast.makeText(getContext(), "네트워크 오류가 발생했습니다. " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getAccessTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("auth", getContext().MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", null);
    }
}
