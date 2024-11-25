package com.example.kickunity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
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

    @POST("api/member/changeName")
    Call<CheckResponse> changeNickname(@Header("Authorization") String authorizationHeader, @Body ChangeNameRequest request);

    @POST("api/member/changePassword")
    Call<CheckResponse> changePassword(
            @Header("Authorization") String authorizationHeader,  // Authorization 헤더
            @Body ChangePasswordRequest request                    // 비밀번호 변경 요청 데이터
    );

    // 카테고리별 게시글 조회
    @GET("api/board/category/{category}")
    Call<List<BoardSummaryResponse>> getBoardsByCategory(@Path("category") String category);

    // 게시글 등록 API
    @POST("api/board")
    Call<BoardDetailResponse> addBoard(@Header("Authorization") String authorizationHeader, @Body AddBoardRequest request);

    // 로그인한 사용자의 게시글 조회
    @GET("api/board/myBoards")
    Call<List<BoardSummaryResponse>> getMyBoards(@Header("Authorization") String token);

    // 게시글 삭제
    @DELETE("api/board/{id}")
    Call<Void> deleteBoard(@Header("Authorization") String token, @Path("id") Long id);

    // 게시글 수정
    @PUT("api/board/{id}")
    Call<BoardDetailResponse> updateBoard(@Header("Authorization") String token, @Path("id") Long id, @Body UpdateBoardRequest request);

    // 게시글 검색
    @GET("api/board/search")
    Call<List<BoardSummaryResponse>> searchBoards(@Query("category") String category, @Query("keyword") String keyword);


    // 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받는 API
    @POST("api/reissue")
    Call<RefreshTokenResponse> reissueAccessToken(@Header("Authorization") String refreshToken);

}
