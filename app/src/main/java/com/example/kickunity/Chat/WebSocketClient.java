package com.example.kickunity.Chat;

import okhttp3.*;
import org.json.JSONObject;
import java.util.concurrent.TimeUnit;

public class WebSocketClient {

    private static final String BASE_URL = "ws://15.165.133.129:8080/ws/conn";  // WebSocket 서버 URL
    private OkHttpClient client;
    private WebSocket webSocket;

    // WebSocketClient 생성자
    public WebSocketClient() {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)  // 연결 타임아웃 설정
                .readTimeout(30, TimeUnit.SECONDS)     // 읽기 타임아웃 설정
                .build();
    }

    // WebSocket 서버 연결
    public void connectWebSocket(String authToken, Long chatRoomId) {
        Request request = new Request.Builder()
                .url(BASE_URL)  // WebSocket URL
                .addHeader("Authorization", "Bearer " + authToken)  // 인증 헤더 추가
                .build();

        // WebSocket 연결
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                // 연결 성공 시
                System.out.println("WebSocket 연결 성공!");

                // 채팅방 참여 메시지 전송
                sendJoinMessage(chatRoomId);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                // 수신된 메시지 처리
                System.out.println("수신 메시지: " + text);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                // WebSocket 연결 실패 시 처리
                System.err.println("WebSocket 연결 실패: " + t.getMessage());
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                // WebSocket 연결 종료 시 처리
                System.out.println("WebSocket 연결 종료: " + reason);
            }
        });
    }

    // 채팅방 참여 메시지 전송
    private void sendJoinMessage(Long chatRoomId) {
        if (webSocket != null) {
            JSONObject joinMessage = new JSONObject();
            try {
                joinMessage.put("chatRoomId", chatRoomId);
                webSocket.send(joinMessage.toString());  // 채팅방 참여 메시지 전송
                System.out.println("채팅방 참여 메시지 전송: " + joinMessage.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 메시지 전송
    public void sendMessage(Long chatRoomId, Long senderId, String message, String messageType) {
        if (webSocket != null) {
            JSONObject messageObject = new JSONObject();
            try {
                messageObject.put("chatRoomId", chatRoomId);  // 채팅방 ID
                messageObject.put("senderId", senderId);      // 발신자 ID
                messageObject.put("message", message);         // 메시지 내용
                messageObject.put("messageType", messageType); // 메시지 타입 (예: TEXT, IMAGE 등)

                webSocket.send(messageObject.toString());  // 메시지 전송
                System.out.println("전송된 메시지: " + messageObject.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("WebSocket이 연결되지 않았습니다.");
        }
    }

    // WebSocket 연결 종료
    public void disconnectWebSocket() {
        if (webSocket != null) {
            webSocket.close(1000, "Client disconnect");  // WebSocket 연결 종료
            System.out.println("WebSocket 연결 종료.");
        }
    }
}
