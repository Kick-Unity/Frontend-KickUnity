package com.example.kickunity.Auth;

import com.example.kickunity.Profile.ChangeNameRequest;
import com.example.kickunity.Profile.ChangePasswordRequest;
import com.example.kickunity.Profile.DeleteMemberRequest;
import com.example.kickunity.Profile.MypageResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface AuthApiService {

    @POST("api/member/emailSend")
    Call<Void> sendVerificationEmail(@Body EmailRequest emailRequest);

    @POST("api/member/emailCheck")
    Call<EmailCheckResponse> verifyCode(@Body EmailCheckRequest request);

    @POST("api/member/join")
    Call<Long> signUpUser(@Body JoinRequest joinRequest);

    @GET("api/member/myPage")
    Call<MypageResponse> getMyPageInfo(
            @Header("Authorization") String authorization,  // Authorization 헤더 추가
            @Query("email") String userEmail  // 사용자 이메일을 쿼리 파라미터로 추가
    );

    @POST("api/member/changeName")
    Call<CheckResponse> changeNickname(@Header("Authorization") String authorizationHeader, @Body ChangeNameRequest request);

    @POST("api/member/changePassword")
    Call<CheckResponse> changePassword(
            @Header("Authorization") String authorizationHeader,
            @Body ChangePasswordRequest request
    );

    @PUT("api/member/deleteMember")
    Call<CheckResponse> deleteAccount(@Header("Authorization") String authorization, @Body DeleteMemberRequest request);

    @POST("api/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("logout")
    Call<Void> logout(@Header("Authorization") String authorization);

    @POST("api/reissue")
    Call<RefreshTokenResponse> reissueAccessToken(@Header("Authorization") String refreshToken);
}
