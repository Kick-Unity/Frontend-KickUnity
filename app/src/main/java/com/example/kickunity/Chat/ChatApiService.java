package com.example.kickunity.Chat;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChatApiService {

    @POST("api/chat/create")
    Call<ChatRoomDTO> createChatRoom(
            @Header("Authorization") String authorizationHeader,  // Authorization 헤더
            @Query("boardId") Long boardId  // 상대방의 사용자 ID
    );

    // 특정 채팅방의 메시지 기록 조회
    @GET("api/chat/messages/{roomId}")
    Call<List<ChatMessageDTO>> getChatMessages(@Path("roomId") Long roomId);

}
