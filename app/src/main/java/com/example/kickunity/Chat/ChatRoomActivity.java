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

        // Back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize RecyclerView and UI components
        chatRecyclerView = findViewById(R.id.chat_recycler);
        messageInput = findViewById(R.id.chat_ET);
        sendButton = findViewById(R.id.chat_send_btn);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize ChatRoom and currentUserId
        initializeChatRoom();

        chatRoomAdapter = new ChatRoomAdapter(new ArrayList<>(), currentUserId, chatRecyclerView);
        chatRecyclerView.setAdapter(chatRoomAdapter);

        // Initialize WebSocket connection
        client = new OkHttpClient();
        if (chatRoomId != null && client != null) {
            connectToWebSocket();
        } else {
            Log.e("ChatRoomActivity", "Client or chatRoomId is null, stopping WebSocket connection.");
            Toast.makeText(this, "Invalid chat room information.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Send message on button click
        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void initializeChatRoom() {
        // Retrieve chat room data from Intent
        ChatRoomDTO currentChatRoom = (ChatRoomDTO) getIntent().getSerializableExtra("chatRoom");

        if (currentChatRoom == null) {
            Log.e("ChatRoomActivity", "currentChatRoom is null.");
            Toast.makeText(this, "Failed to retrieve chat room information.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        chatRoomId = currentChatRoom.getRoomId();
        ChatRoomDTO.MemberDTO currentUser = currentChatRoom.getcurrentUser();
        ChatRoomDTO.MemberDTO otherUser = currentChatRoom.getotherUser();

        if (chatRoomId == null || currentUser == null || otherUser == null) {
            Log.e("ChatRoomActivity", "Invalid chat room information.");
            Toast.makeText(this, "Invalid chat room information.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentUserId = currentUser.getId();
    }

    private void connectToWebSocket() {
        if (client == null) {
            Log.e("ChatRoomActivity", "OkHttpClient is not initialized.");
            return;
        }

        String url = "ws://15.165.133.129:8080/ws/conn";
        try {
            URI uri = new URI(url + "?chatRoomId=" + chatRoomId);
            Log.d("ChatRoomActivity", "Connecting to WebSocket with URL: " + uri.toString());
            Log.d("ChatRoomActivity", "Authorization Token: " + getToken());

            Request request = new Request.Builder()
                    .url(uri.toString())
                    .addHeader("Authorization", "Bearer " + getToken()) // Add Authorization header
                    .build();

            webSocket = client.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    Log.d("WebSocket", "WebSocket opened");
                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    Log.d("WebSocket", "Message received: " + text);
                    handleWebSocketMessage(text);
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    Log.e("WebSocket", "Connection failed: " + t.getMessage(), t);
                    if (response != null) {
                        Log.e("WebSocket", "Failure Response: " + response.toString());
                    }
                }

                @Override
                public void onClosed(WebSocket webSocket, int code, String reason) {
                    Log.d("WebSocket", "WebSocket closed. Code: " + code + ", Reason: " + reason);
                }
            });

        } catch (URISyntaxException e) {
            Log.e("ChatRoomActivity", "Error creating WebSocket URI: " + e.getMessage(), e);
        }
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString();

        if (messageText.trim().isEmpty()) {
            Log.e("Chat", "Cannot send empty message.");
            return;
        }

        // Create a new ChatMessage object
        ChatMessage message = new ChatMessage();
        message.setChatRoomId(chatRoomId);
        message.setMessage(messageText);
        message.setMessageType("TALK"); // You can change this to any other message type you need
        message.setSenderId(currentUserId);

        // Send the message to WebSocket
        sendMessageToWebSocket(message);
    }

    private void sendMessageToWebSocket(ChatMessage message) {
        try {
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("chatRoomId", message.getChatRoomId());
            jsonMessage.put("message", message.getMessage());
            jsonMessage.put("messageType", message.getMessageType());
            jsonMessage.put("senderId", message.getSenderId());

            if (webSocket != null) {
                webSocket.send(jsonMessage.toString());
                messageInput.setText(""); // Clear input field
            } else {
                Log.e("WebSocket", "WebSocket is not connected.");
            }
        } catch (JSONException e) {
            Log.e("WebSocket", "Error creating JSON for message: " + e.getMessage(), e);
        }
    }

    private void handleWebSocketMessage(String text) {
        try {
            if (isJsonValid(text)) {
                JSONObject jsonMessage = new JSONObject(text);
                String senderName = jsonMessage.getString("senderName");
                String message = jsonMessage.getString("message");
                String time = getCurrentTime();
                Long senderId = jsonMessage.getLong("senderId");

                runOnUiThread(() -> {
                    // 새 메시지를 추가
                    chatRoomAdapter.addMessage(senderId, senderName, message, time);

                    // RecyclerView의 마지막 항목으로 자동 스크롤
                    chatRecyclerView.scrollToPosition(chatRoomAdapter.getItemCount() - 1);
                });
            } else {
                runOnUiThread(() -> Toast.makeText(this, "서버 메시지: " + text, Toast.LENGTH_SHORT).show());
            }
        } catch (JSONException e) {
            Log.e("WebSocket", "JSON 파싱 오류: " + e.getMessage(), e);
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
