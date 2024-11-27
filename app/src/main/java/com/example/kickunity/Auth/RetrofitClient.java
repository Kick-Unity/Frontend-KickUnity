package com.example.kickunity.Auth;
import com.example.kickunity.Board.BoardApiService;
import com.example.kickunity.Team.TeamApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    static final String BASE_URL = "http://15.165.133.129:8080/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
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
}
