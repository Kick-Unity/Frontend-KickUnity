package com.example.kickunity.Chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kickunity.R;

import java.util.List;

import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// 채팅방 목록을 보여주는 화면을 담당
public class ChatFragment extends Fragment implements ChatRoomListAdapter.OnChatRoomClickListener {

    private RecyclerView recyclerViewChatRooms;
    private ChatRoomListAdapter chatRoomListAdapter;
    private String authToken;
    private Retrofit retrofit;
    private ChatApiService chatApiService;
    private WebSocketClient webSocketClient;
    private Long currentChatRoomId; // 현재 채팅방 ID
    private Long currentUserId; // 현재 사용자 ID

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // RecyclerView 초기화
        recyclerViewChatRooms = view.findViewById(R.id.recyclerViewChatRooms);
        recyclerViewChatRooms.setLayoutManager(new LinearLayoutManager(getActivity()));

        chatRoomListAdapter = new ChatRoomListAdapter(this);
        recyclerViewChatRooms.setAdapter(chatRoomListAdapter);

        // WebSocketClient 초기화 (이곳에서 초기화 해줘야 합니다.)
        webSocketClient = new WebSocketClient();

        // Retrofit 초기화 및 API 호출
        retrofit = new Retrofit.Builder()
                .baseUrl("http://15.165.133.129:8080/")  // 서버 주소
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        chatApiService = retrofit.create(ChatApiService.class);

        // SharedPreferences에서 액세스 토큰을 가져옵니다.
        authToken = getAccessTokenFromSharedPreferences();

        if (authToken != null && !authToken.isEmpty()) {
            // 서버에서 채팅방 목록을 가져오는 메소드 호출
            fetchChatRooms(authToken);  // authToken을 전달하여 API 호출
        } else {
            // 액세스 토큰이 없을 경우 오류 처리
            Toast.makeText(getActivity(), "토큰을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void fetchChatRooms(String authToken) {
        // API 호출
        chatApiService.getChatRooms("Bearer " + authToken).enqueue(new Callback<List<ChatRoomDTO>>() {
            @Override
            public void onResponse(Call<List<ChatRoomDTO>> call, Response<List<ChatRoomDTO>> response) {
                if (response.isSuccessful()) {
                    List<ChatRoomDTO> chatRoomList = response.body();
                    if (chatRoomList != null && !chatRoomList.isEmpty()) {
                        // 서버에서 받은 데이터로 어댑터 업데이트
                        chatRoomListAdapter.setChatRooms(chatRoomList);
                    } else {
                        // 채팅방이 없을 경우 처리
                        Toast.makeText(getActivity(), "채팅방이 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 응답 실패 시 처리
                    Toast.makeText(getActivity(), "서버 오류: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ChatRoomDTO>> call, Throwable t) {
                // 네트워크 오류 처리
                Toast.makeText(getActivity(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ChatFragment", "Error fetching chat rooms", t);
            }
        });
    }

    @Override
    public void onChatRoomClick(ChatRoomDTO chatRoomDTO) {
        // 채팅방 클릭 시, currentChatRoomId와 currentUserId 설정
        currentChatRoomId = chatRoomDTO.getRoomId();  // 채팅방 ID
        currentUserId = chatRoomDTO.getcurrentUser().getId();  // 현재 사용자 ID
        Log.d("ChatFragment", "Selected ChatRoom ID: " + currentChatRoomId);
        Log.d("ChatFragment", "Selected User ID: " + currentUserId);

        // 이미 생성된 채팅방을 선택하면 ChatRoomDetailsActivity로 이동
        Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
        intent.putExtra("chatRoom", chatRoomDTO); // 기존 채팅방 정보
        startActivity(intent);
    }

    @Override
    public void onExitButtonClick(ChatRoomDTO chatRoomDTO) {
        currentChatRoomId = chatRoomDTO.getRoomId();  // 채팅방 ID
        currentUserId = chatRoomDTO.getcurrentUser().getId();  // 현재 사용자 ID
        Log.d("ChatFragment", "Selected ChatRoom ID: " + currentChatRoomId);
        Log.d("ChatFragment", "Selected User ID: " + currentUserId);

        showExitDialog();
    }

    private void showExitDialog() {
        new AlertDialog.Builder(getActivity())
                .setMessage("채팅방을 나가겠습니까?")
                .setCancelable(true)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 로그로 변수 상태를 출력하여 확인
                        Log.d("ChatFragment", "currentChatRoomId: " + currentChatRoomId);
                        Log.d("ChatFragment", "currentUserId: " + currentUserId);
                        Log.d("ChatFragment", "webSocketClient: " + webSocketClient);

                        // WebSocket이 연결되어 있고, 채팅방 정보가 있는지 확인 후 나가기 요청
                        if (currentChatRoomId != null && currentUserId != null && webSocketClient != null) {
                            // WebSocket 연결 요청
                            String token = getAccessTokenFromSharedPreferences();  // 액세스 토큰 가져오기
                            if (token != null) {
                                // WebSocket 연결
                                webSocketClient.connectWebSocket(authToken, currentChatRoomId, currentUserId);

                                // WebSocket 연결 후, 메시지 전송
                                webSocketClient.sendMessage(currentChatRoomId, currentUserId, "채팅방 나가기", "LEAVE");

                                // 채팅방 목록에서 해당 채팅방 제거
                                chatRoomListAdapter.removeChatRoom(currentChatRoomId); // 어댑터에서 해당 채팅방 제거

                                // 사용자에게 알림
                                if (getActivity() != null) {
                                    Toast.makeText(getActivity(), "채팅방을 나갔습니다.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d("ChatFragment", "액세스 토큰이 없습니다.");
                                if (getActivity() != null) {
                                    Toast.makeText(getActivity(), "액세스 토큰이 없습니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            // WebSocket이 연결되지 않거나, 채팅방 정보가 없을 때 로그를 추가로 출력
                            Log.d("ChatFragment", "채팅방 정보가 없거나 WebSocket 연결이 되지 않았습니다.");
                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), "채팅방 정보가 없거나 WebSocket 연결이 되지 않았습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }


    // SharedPreferences에서 액세스 토큰 가져오기
    private String getAccessTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("auth", getContext().MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", null); // "accessToken" 키로 토큰을 가져옵니다.
    }
}
