package com.example.kickunity.Auth;

import com.example.kickunity.Board.BoardApiService;
import com.example.kickunity.Chat.ChatApiService;
import com.example.kickunity.Team.TeamApiService;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    static final String BASE_URL = "http://15.165.133.129:8080/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL) // 기본 URL
                    .addConverterFactory(GsonConverterFactory.create()) // JSON을 객체로 변환
                    .client(new OkHttpClient()) // OkHttpClient 사용
                    .build();
        }
        return retrofit;
    }

    public static TeamApiService getTeamApiService() {
        return getRetrofitInstance().create(TeamApiService.class);
    }

    public static AuthApiService getAuthApiService() {
        return getRetrofitInstance().create(AuthApiService.class);
    }

    public static BoardApiService getBoardApiService() {
        return getRetrofitInstance().create(BoardApiService.class);
    }

    public static ChatApiService getChatApiService() {
        return getRetrofitInstance().create(ChatApiService.class);
    }
}
