package com.example.kickunity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    String BASE_URL = "http://15.165.133.129:8080/";  // 여기에 기본 URL을 정의

    @POST("api/join")
    Call<Long> signUpUser(@Body JoinRequest joinRequest); // Call 객체를 선언하여 HTTP 요청을 웹 서버로 보냄
      // < > 안에 자료형은 Json 데이터를 < > 안에 자료형으로 받겠다는 의미

    @Headers("Content-Type: application/json")
    @POST("api/login")  // 서버 URL의 로그인 엔드포인트
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    /*
    @GET("api/user/team") // 팀 여부 확인 API 엔드포인트
    -> GET 어노테이션은 서버 데이터를 조회하고 싶다는 의미, 내부 값으로는 BASEURL 뒤쪽에 위치한 디렉터리
    Call<TeamResponse> checkUserTeam(@Query("userId") String userId);
    // userId는 실제로 사용자의 고유 ID로 대체
    */
}
