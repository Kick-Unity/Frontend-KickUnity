package com.example.kickunity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    String BASE_URL = "http://15.165.133.129:8080/";  // 여기에 기본 URL을 정의

    @POST("api/join")
    Call<Long> signUpUser(@Body JoinRequest joinRequest);

    @Headers("Content-Type: application/json")
    @POST("api/login")  // 서버 URL의 로그인 엔드포인트
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
}
