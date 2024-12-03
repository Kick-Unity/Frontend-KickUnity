package com.example.kickunity.Chat;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kickunity.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.net.URI;
import java.net.URISyntaxException;

public class ChatRoomActivity extends AppCompatActivity {

    private Long chatRoomId;
    private Long currentUserId;
    private WebSocket webSocket;
    private OkHttpClient client;
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private Button sendButton;
    private ChatRoomAdapter chatRoomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        // 뒤로 가기 버튼 설정
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish(); // 현재 Activity 종료
        });

        // RecyclerView 및 UI 요소 초기화
        chatRecyclerView = findViewById(R.id.chat_recycler);
        messageInput = findViewById(R.id.chat_ET);
        sendButton = findViewById(R.id.chat_send_btn);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 채팅방 초기화 및 currentUserId 설정
        initializeChatRoom();

        // currentUserId가 초기화된 후 ChatRoomAdapter 생성
        chatRoomAdapter = new ChatRoomAdapter(new ArrayList<>(), currentUserId);  // currentUserId 추가
        chatRecyclerView.setAdapter(chatRoomAdapter);

        // OkHttpClient 초기화
        client = new OkHttpClient();

        // WebSocket 연결 시도
        if (chatRoomId != null && client != null) {
            connectToWebSocket();
        } else {
            Log.e("ChatRoomActivity", "client 또는 chatRoomId가 null입니다. WebSocket 연결을 중단합니다.");
            Toast.makeText(this, "채팅방 정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void initializeChatRoom() {
        // Intent로부터 데이터 가져오기
        ChatRoomDTO currentChatRoom = (ChatRoomDTO) getIntent().getSerializableExtra("chatRoom");

        if (currentChatRoom == null) {
            Log.e("ChatRoomActivity", "currentChatRoom이 null입니다.");
            Toast.makeText(this, "채팅방 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        chatRoomId = currentChatRoom.getRoomId();
        ChatRoomDTO.MemberDTO currentUser = currentChatRoom.getcurrentUser();
        ChatRoomDTO.MemberDTO otherUser = currentChatRoom.getotherUser();

        if (chatRoomId == null || currentUser == null || otherUser == null) {
            Log.e("ChatRoomActivity", "유효하지 않은 채팅방 정보입니다.");
            Toast.makeText(this, "유효하지 않은 채팅방 정보입니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentUserId = currentUser.getId();

        Log.d("ChatRoomActivity", "chatRoomId: " + chatRoomId);
        Log.d("ChatRoomActivity", "Current User: " + currentUser.getId());
        Log.d("ChatRoomActivity", "Other User: " + otherUser.getId());
    }

    private void connectToWebSocket() {
        if (client == null) {
            Log.e("ChatRoomActivity", "OkHttpClient가 초기화되지 않았습니다.");
            return;
        }

        String url = "ws://15.165.133.129:8080/ws/conn"; // 서버 주소
        try {
            URI uri = new URI(url + "?chatRoomId=" + chatRoomId);

            Log.d("ChatRoomActivity", "Connecting to WebSocket with URL: " + uri.toString());
            Log.d("ChatRoomActivity", "Authorization Token: " + getToken());

            Request request = new Request.Builder()
                    .url(uri.toString())
                    .addHeader("Authorization", "Bearer " + getToken())
                    .build();

            webSocket = client.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    Log.d("WebSocket", "WebSocket opened");
                    sendJoinMessage();  // 채팅방에 참여하는 메시지를 보냄
                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    try {
                        // 수신된 메시지가 JSON 형식인지 확인
                        if (isJsonValid(text)) {
                            JSONObject jsonMessage = new JSONObject(text);
                            // 예시: WebSocket 메시지를 수신한 후, addMessage() 호출
                            String senderId = jsonMessage.getString("senderId");
                            String senderName = jsonMessage.getString("senderName");  // senderName을 적절히 받기
                            String message = jsonMessage.getString("message");
                            String time = getCurrentTime();

                            // addMessage() 호출
                            runOnUiThread(() -> chatRoomAdapter.addMessage(Long.parseLong(senderId), senderName, message, time));
                        } else {
                            Log.d("ChatRoomActivity", "Received non-JSON message: " + text);
                            runOnUiThread(() -> {
                                Toast.makeText(ChatRoomActivity.this, "서버 메시지: " + text, Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (JSONException e) {
                        Log.e("ChatRoomActivity", "수신 메시지 처리 오류: " + e.getMessage());
                    }
                }

                private boolean isJsonValid(String text) {
                    try {
                        new JSONObject(text);
                        return true;
                    } catch (JSONException ex) {
                        return false;
                    }
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    Log.e("WebSocket", "Error: " + t.getMessage(), t);
                }

                @Override
                public void onClosed(WebSocket webSocket, int code, String reason) {
                    Log.d("WebSocket", "WebSocket closed: " + reason);
                }
            });

        } catch (URISyntaxException e) {
            Log.e("ChatRoomActivity", "WebSocket URI 생성 중 오류 발생: " + e.getMessage());
        }
    }

    private void sendJoinMessage() {
        if (webSocket != null) {
            JSONObject joinMessage = new JSONObject();
            try {
                // 채팅방 참여 메시지의 내용은 null 또는 빈 문자열로 설정
                joinMessage.put("chatRoomId", chatRoomId);
                joinMessage.put("senderId", currentUserId); // 현재 사용자 아이디 추가
                joinMessage.put("message", ""); // 참여 메시지의 내용은 빈 문자열로 설정
                joinMessage.put("messageType", "JOIN"); // 메시지 타입을 JOIN으로 설정

                Log.d("ChatRoomActivity", "채팅방 참여 메시지: " + joinMessage.toString()); // 로그 추가
                webSocket.send(joinMessage.toString());
                Log.d("ChatRoomActivity", "채팅방 참여 메시지 전송: " + chatRoomId);
            } catch (JSONException e) {
                Log.e("ChatRoomActivity", "참여 메시지 생성 오류: " + e.getMessage());
            }
        }
    }


    private void sendMessage() {
        String messageText = messageInput.getText().toString();

        // 빈 메시지 확인
        if (messageText == null || messageText.trim().isEmpty()) {
            Log.e("Chat", "빈 메시지는 전송할 수 없습니다.");
            return;
        }

        // 메시지 객체 생성
        ChatMessage message = new ChatMessage();
        message.setChatRoomId(chatRoomId);        // 채팅방 아이디
        message.setMessage(messageText);          // 메시지 내용
        message.setMessageType(MessageType.TALK); // 메시지 타입 설정
        message.setSenderId(currentUserId);      // 현재 사용자 아이디 추가

        Log.d("Chat", "Sending message: " + message.toString()); // 로그 추가
        sendMessageToWebSocket(message);
    }

    // WebSocket 메시지 전송 메서드
    private void sendMessageToWebSocket(ChatMessage message) {
        try {
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("chatRoomId", message.getChatRoomId());
            jsonMessage.put("message", message.getMessage());
            jsonMessage.put("messageType", message.getMessageType());
            jsonMessage.put("senderId", message.getSenderId()); // senderId 추가

            Log.d("Chat", "Sending message JSON: " + jsonMessage.toString()); // 로그 추가
            if (webSocket != null) {
                webSocket.send(jsonMessage.toString());
                Log.d("Chat", "메시지 전송 성공: " + jsonMessage.toString());
                messageInput.setText(""); // 입력 필드 초기화
            } else {
                Log.e("Chat", "WebSocket이 연결되지 않았습니다.");
            }
        } catch (JSONException e) {
            Log.e("Chat", "메시지 JSON 생성 오류: " + e.getMessage());
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    private String getToken() {
        return getSharedPreferences("auth", MODE_PRIVATE).getString("accessToken", null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, "Activity destroyed");
        }
    }
}
