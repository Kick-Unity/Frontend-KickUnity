package com.example.kickunity.Chat;

import okhttp3.*;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class WebSocketClient {

    private static final String BASE_URL = "ws://15.165.133.129:8080/ws/conn";  // WebSocket 서버 URL
    private OkHttpClient client;
    private WebSocket webSocket;
    private boolean isConnected = false; // 연결 상태 추적
    private int retryAttempts = 0; // 재시도 횟수

    // 최초 연결인지 여부를 추적하는 변수
    private boolean isFirstConnection = true;

    private MessageListener messageListener;  // 메시지 리스너 변수


    // WebSocketClient 생성자
    public WebSocketClient() {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)  // 연결 타임아웃 설정
                .readTimeout(30, TimeUnit.SECONDS)     // 읽기 타임아웃 설정
                .build();
    }

    // 메시지 리스너 인터페이스 정의
    public interface MessageListener {
        void onMessageReceived(String text);  // 메시지를 수신했을 때 호출되는 메서드
    }

    // 메시지 리스너 설정 메서드
    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    // 서버에서 받은 메시지 처리
    private void handleReceivedMessage(String text) {
        if (messageListener != null) {
            messageListener.onMessageReceived(text);  // 메시지 리스너 호출
        }
    }

    // WebSocket 서버 연결
    public void connectWebSocket(String authToken, Long chatRoomId, Long senderId) {
        System.out.println("WebSocket 연결 시도 중..."); // 연결 시도 로그 추가

        // 기존 연결이 있다면 종료
        if (webSocket != null && isConnected) {
            System.out.println("기존 WebSocket 연결 종료...");
            disconnectWebSocket();
        }

        // WebSocket 연결 요청 URL 및 헤더 설정
        Request request = new Request.Builder()
                .url(BASE_URL + "?chatRoomId=" + chatRoomId)  // WebSocket URL에 chatRoomId 추가
                .addHeader("Authorization", "Bearer " + authToken)  // 인증 헤더 추가
                .build();

        // WebSocket 연결
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                isConnected = true;
                retryAttempts = 0;  // 연결 성공 시 재시도 횟수 초기화
                System.out.println("WebSocket 연결 성공!"); // 연결 성공 로그 추가

                // 최초 연결일 경우에만 채팅방 참여 메시지 전송
                if (isFirstConnection) {
                    sendMessage(chatRoomId, senderId, "채팅이 연결되었습니다!", "JOIN");
                    isFirstConnection = false;  // 이후 재접속에서는 sendInitialMessage가 실행되지 않도록 false로 설정
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                // 수신된 메시지 처리
                System.out.println("수신된 메시지: " + text);  // 수신된 메시지 로그 추가
                handleReceivedMessage(text); // 서버에서 받은 메시지를 처리하는 메서드 호출
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                isConnected = false;
                System.err.println("WebSocket 연결 실패: " + t.getMessage());
                t.printStackTrace();

                // 재시도 로직 (예시: 5초 후 재시도, 최대 3번 시도)
                retryConnection(authToken, chatRoomId, senderId);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                isConnected = false;
                System.out.println("WebSocket 연결 종료: " + reason);
            }
        });
    }

    // 서버로 메시지 전송
    public void sendMessage(Long chatRoomId, Long senderId, String message, String messageType) {
        if (webSocket != null && isConnected) {
            try {
                // 메시지 객체 생성
                JSONObject messageObject = new JSONObject();
                messageObject.put("chatRoomId", chatRoomId);  // 채팅방 ID
                messageObject.put("senderId", senderId);      // 발신자 ID
                messageObject.put("message", message);         // 메시지 내용
                messageObject.put("messageType", messageType); // 메시지 타입 (JOIN/TALK/LEAVE)

                // 전송될 메시지 로그
                System.out.println("전송할 메시지 준비: " + messageObject.toString());

                // 웹소켓으로 메시지 전송
                webSocket.send(messageObject.toString());  // 메시지 전송

                // 전송된 메시지 확인
                System.out.println("전송된 메시지: " + messageObject);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("메시지 전송 중 오류 발생");
            }
        } else {
            System.err.println("WebSocket이 연결되지 않았습니다.");
        }
    }

    // WebSocket 재시도 로직 (최대 3번 시도)
    private void retryConnection(String authToken, Long chatRoomId, Long senderId) {
        if (retryAttempts < 3) {
            retryAttempts++;
            try {
                System.out.println("WebSocket 재시도 " + retryAttempts + "번 시도 중...");
                Thread.sleep(5000); // 5초 후 재시도
                connectWebSocket(authToken, chatRoomId, senderId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("WebSocket 재시도 횟수 초과. 더 이상 재시도하지 않습니다.");
        }
    }

    // WebSocket 연결 종료
    public void disconnectWebSocket() {
        if (webSocket != null && isConnected) {
            try {
                webSocket.close(1000, "Client disconnect");  // WebSocket 연결 종료
                System.out.println("WebSocket 연결 종료.");
            } catch (Exception e) {
                System.err.println("WebSocket 종료 중 오류 발생: " + e.getMessage());
            }
        } else {
            System.err.println("WebSocket 연결이 이미 종료되었거나 연결되지 않았습니다.");
        }
    }
}
