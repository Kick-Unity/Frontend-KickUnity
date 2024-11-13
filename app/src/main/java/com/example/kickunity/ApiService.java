package com.example.kickunity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    String BASE_URL = "http://15.165.133.129:8080/";

    // 이메일 인증 요청 API
    @Headers("Content-Type: application/json")
    @POST("api/member/emailSend")  // 실제 서버의 이메일 전송 URL로 변경
    Call<Void> sendVerificationEmail(@Body EmailRequest emailRequest);

    @POST("api/member/emailCheck")
    Call<EmailCheckResponse> verifyCode(@Body EmailCheckRequest request);

    @POST("api/member/join")
    Call<Long> signUpUser(@Body JoinRequest joinRequest);

    @Headers("Content-Type: application/json")
    @POST("api/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

}
