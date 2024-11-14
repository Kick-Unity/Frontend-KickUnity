package com.example.kickunity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    String BASE_URL = "http://15.165.133.129:8080/";

    // 이메일 인증 요청 API
    @Headers("Content-Type: application/json")
    @POST("api/member/emailSend")
    Call<Void> sendVerificationEmail(@Body EmailRequest emailRequest);

    @POST("api/member/emailCheck")
    Call<EmailCheckResponse> verifyCode(@Body EmailCheckRequest request);

    @POST("api/member/join")
    Call<Long> signUpUser(@Body JoinRequest joinRequest);

    @Headers("Content-Type: application/json")
    @POST("api/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // GET 요청 시 Authorization 헤더를 추가
    @GET("api/member/myPage")
    Call<MypageResponse> getMyPageInfo(
            @Header("Authorization") String authorization,  // Authorization 헤더 추가
            @Query("email") String userEmail  // 사용자 이메일을 쿼리 파라미터로 추가
    );
}
