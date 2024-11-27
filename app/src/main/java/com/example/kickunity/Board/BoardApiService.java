package com.example.kickunity.Board;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface BoardApiService {

    @GET("api/board/category/{category}")
    Call<List<BoardSummaryResponse>> getBoardsByCategory(@Path("category") String category);

    @POST("api/board")
    Call<BoardDetailResponse> addBoard(@Header("Authorization") String authorizationHeader, @Body AddBoardRequest request);

    @GET("api/board/myBoards")
    Call<List<BoardSummaryResponse>> getMyBoards(@Header("Authorization") String token);

    @DELETE("api/board/{id}")
    Call<Void> deleteBoard(@Header("Authorization") String token, @Path("id") Long id);

    // 게시글 수정 API
    @PUT("api/board/{id}")
    Call<BoardDetailResponse> updateBoard(
            @Header("Authorization") String authorizationHeader,
            @Path("id") Long id,
            @Body UpdateBoardRequest request
    );

    @GET("api/board/search")
    Call<List<BoardSummaryResponse>> searchBoards(@Query("category") String category, @Query("keyword") String keyword);

    @GET("api/board/{id}")
    Call<BoardDetailResponse> getBoardDetail(@Path("id") Long id);

}