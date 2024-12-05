package com.example.kickunity.Team;

import com.example.kickunity.Auth.CheckResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.*;

public interface TeamApiService {

    @POST("api/team/create")
    Call<Long> createTeam(@Header("Authorization") String token, @Body AddTeamRequest request);

    // 팀원 추가
    @POST("api/team/{teamId}/addMember/{memberEmail}")
    Call<String> addMember(
            @Header("Authorization") String authorizationHeader,
            @Path("teamId") Long teamId,
            @Path("memberEmail") String memberEmail // Email as query parameter
    );

    // 팀원 삭제
    @DELETE("api/team/{teamId}/removeMember/{memberEmail}")
    Call<String> removeMember(
            @Header("Authorization") String authorizationHeader,
            @Path("teamId") Long teamId,
            @Path("memberEmail") String memberEmail // Email as query parameter
    );

    @DELETE("api/team/{teamId}")
    Call<String> deleteTeam(@Header("Authorization") String token, @Path("teamId") Long teamId);

    @GET("api/team/searchTeam")
    Call<List<TeamSummaryResponse>> searchTeamByName(@Query("teamName") String teamName);

    @GET("api/team/{teamId}")
    Call<TeamDetailResponse> getTeamDetailWithToken(
            @Path("teamId") Long teamId, // teamId를 URL 경로로 전달
            @Header("Authorization") String accessToken // Authorization 헤더에 액세스 토큰 전달
    );

    // 사용자 팀 여부 확인 API
    @GET("api/team/myTeam")
    Call<Long> getUserTeamStatus(@Header("Authorization") String authorizationHeader);

    @PUT("api/team/changeTeamName/{teamId}")
    Call<ResponseBody> updateTeamName(
            @Header("Authorization") String authorizationHeader,
            @Path("teamId") Long teamId,
            @Body UpdateTeamRequest request
    );

    @PUT("api/team/changeTeamCategory/{teamId}")
    Call<ResponseBody> updateTeamCategory(
            @Header("Authorization") String authorizationHeader,
            @Path("teamId") Long teamId,
            @Body UpdateTeamRequest request
    );

    @PUT("api/team/changeTeamStartDate/{teamId}")
    Call<ResponseBody> updateTeamStartDate(
            @Header("Authorization") String authorizationHeader,
            @Path("teamId") Long teamId,
            @Body UpdateTeamRequest request
    );

    @PUT("api/team/changeTeamRegion/{teamId}")
    Call<ResponseBody> updateTeamRegion(
            @Header("Authorization") String authorizationHeader,
            @Path("teamId") Long teamId,
            @Body UpdateTeamRequest request
    );

    @PUT("api/team/changeTeamAge/{teamId}")
    Call<ResponseBody> updateTeamAge(
            @Header("Authorization") String authorizationHeader,
            @Path("teamId") Long teamId,
            @Body UpdateTeamRequest request
    );

    @PUT("api/team/changeTeamDescription/{teamId}")
    Call<ResponseBody> updateTeamDescription(
            @Header("Authorization") String authorizationHeader,
            @Path("teamId") Long teamId,
            @Body UpdateTeamRequest request
    );

    @PUT("api/team/changeTeamSize/{teamId}")
    Call<ResponseBody> updateTeamSize(
            @Header("Authorization") String authorizationHeader,
            @Path("teamId") Long teamId,
            @Body UpdateTeamRequest request
    );

}
