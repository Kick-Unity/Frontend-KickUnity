package com.example.kickunity.Auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogoutHelper {

    public static void logout(Context context, String cookieHeader) {
        // Retrofit 인스턴스를 통해 로그아웃 요청
        AuthApiService authApiService = RetrofitClient.getRetrofitInstance().create(AuthApiService.class);

        // 로그아웃 요청을 보내는 Call 객체 생성
        Call<Void> call = authApiService.logout(cookieHeader);

        // 로그아웃 요청을 비동기로 보냄
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // 로그아웃 성공 시 처리
                    clearUserDataFromSharedPreferences(context);
                    Toast.makeText(context, "로그아웃 성공", Toast.LENGTH_SHORT).show();
                } else {
                    // 로그아웃 실패 시 처리
                    Toast.makeText(context, "로그아웃 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // 실패 시 네트워크 오류 처리
                if (t instanceof java.io.IOException) {
                    Toast.makeText(context, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "로그아웃 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // SharedPreferences에서 사용자 정보 삭제
    private static void clearUserDataFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }
}
