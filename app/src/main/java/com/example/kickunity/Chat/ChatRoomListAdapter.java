package com.example.kickunity.Chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kickunity.R;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomListAdapter extends RecyclerView.Adapter<ChatRoomListAdapter.ChatRoomViewHolder> {

    private List<ChatRoomDTO> chatRoomDataList;  // 채팅방 리스트
    private OnChatRoomClickListener onChatRoomClickListener;  // 클릭 리스너 변수

    // Constructor to initialize the list (빈 리스트로 시작)
    public ChatRoomListAdapter(OnChatRoomClickListener onChatRoomClickListener) {
        this.chatRoomDataList = new ArrayList<>();
        this.onChatRoomClickListener = onChatRoomClickListener;  // 리스너 초기화
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_room, parent, false);
        return new ChatRoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int position) {
        // Get the ChatRoomDTO object for the current position
        ChatRoomDTO chatRoom = chatRoomDataList.get(position);

        // Get the name of the other user from the ChatRoomDTO
        String otherUserName = chatRoom.getotherUser().getName();
        String chatRoomLabel = "[" + otherUserName + "]님과의 채팅";
        holder.userNameTextView.setText(chatRoomLabel);

        // 프로필 이미지 설정 (기본 이미지 또는 실제 이미지 로딩)
        holder.profileImageView.setImageResource(R.drawable.profile2); // Placeholder profile image

        // 채팅방 클릭 시 처리
        holder.itemView.setOnClickListener(v -> {
            if (onChatRoomClickListener != null) {
                onChatRoomClickListener.onChatRoomClick(chatRoom);  // ChatRoomDTO 객체를 전달
            }
        });

        // 나가기 버튼 클릭 시 처리
        holder.exitButton.setOnClickListener(v -> {
            if (onChatRoomClickListener != null) {
                onChatRoomClickListener.onExitButtonClick(chatRoom);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatRoomDataList.size();
    }

    // 데이터 설정
    public void setChatRooms(List<ChatRoomDTO> chatRooms) {
        this.chatRoomDataList = chatRooms;
        notifyDataSetChanged();  // 데이터 변경을 RecyclerView에 반영
    }


    // 채팅방을 리스트에서 제거하는 메서드
    public void removeChatRoom(Long chatRoomId) {
        // 채팅방 리스트에서 해당 chatRoomId를 가진 채팅방을 찾아서 제거
        for (int i = 0; i < chatRoomDataList.size(); i++) {
            if (chatRoomDataList.get(i).getRoomId().equals(chatRoomId)) {
                chatRoomDataList.remove(i);
                notifyItemRemoved(i);  // 해당 위치의 아이템을 제거했다고 RecyclerView에 알림
                break;
            }
        }
    }


    // ViewHolder class to hold the views for each item
    public static class ChatRoomViewHolder extends RecyclerView.ViewHolder {

        TextView userNameTextView;  // 채팅방의 상대방 이름
        ImageView profileImageView;  // 상대방의 프로필 이미지
        ImageButton exitButton;  // 나가기 버튼

        public ChatRoomViewHolder(View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.receiver_name);  // 상대방 이름을 담는 TextView
            profileImageView = itemView.findViewById(R.id.profile);  // 프로필 이미지 아이콘
            exitButton = itemView.findViewById(R.id.exitButton);  // 나가기 버튼
        }
    }

    // Interface to handle item click events
    public interface OnChatRoomClickListener {
        void onChatRoomClick(ChatRoomDTO chatRoomDTO);
        void onExitButtonClick(ChatRoomDTO chatRoomDTO);
    }
}
