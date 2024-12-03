package com.example.kickunity.Chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kickunity.R;

public class ChatFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_chat,container,false);
        return v;

    }
}

/*
package com.example.kickunity.Chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kickunity.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerViewChatMessages;
    private ChatMessageAdapter chatMessageAdapter;
    private List<ChatMessageDTO> chatMessages;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerViewChatMessages = view.findViewById(R.id.recyclerViewChatRooms);
        recyclerViewChatMessages.setLayoutManager(new LinearLayoutManager(getContext()));

        loadChatMessages();

        return view;
    }

    private void loadChatMessages() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://15.165.133.129:8080/")  // 서버 URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ChatApiService apiService = retrofit.create(ChatApiService.class);

        // 실제 채팅방 ID를 가져와서 서버 요청
        Call<List<ChatMessageDTO>> call = apiService.getChatMessages(getChatRoomId);

        call.enqueue(new Callback<List<ChatMessageDTO>>() {
            @Override
            public void onResponse(Call<List<ChatMessageDTO>> call, Response<List<ChatMessageDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chatMessages = response.body();
                    chatMessageAdapter = new ChatMessageAdapter(chatMessages);
                    recyclerViewChatMessages.setAdapter(chatMessageAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<ChatMessageDTO>> call, Throwable t) {
                // 실패 처리
            }
        });
    }

}
*/