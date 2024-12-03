package com.example.kickunity.Chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kickunity.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ChatMessageViewHolder> {

    private List<ChatMessageDTO> chatMessages;

    public ChatMessageAdapter(List<ChatMessageDTO> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @Override
    public ChatMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 새로운 항목 뷰를 인플레이트합니다
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_room, parent, false);
        return new ChatMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatMessageViewHolder holder, int position) {
        // 각 메시지 항목을 데이터로 채웁니다
        ChatMessageDTO message = chatMessages.get(position);

        // receiver_name을 송신자의 이름으로 설정
        holder.receiverName.setText(message.getSenderName());

        // receiver_content를 메시지 내용으로 설정
        holder.receiverContent.setText(message.getMessage());

        // receiver_time을 LocalDateTime으로 포맷팅하여 표시
        LocalDateTime createdAt = message.getCreatedAt();
        if (createdAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String formattedTime = createdAt.format(formatter);
            holder.receiverTime.setText(formattedTime);
        } else {
            holder.receiverTime.setText("00:00");  // 기본값 처리
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();  // 채팅 메시지 목록의 크기 반환
    }

    public static class ChatMessageViewHolder extends RecyclerView.ViewHolder {
        TextView receiverName;
        TextView receiverContent;
        TextView receiverTime;
        ImageView profileImage;

        public ChatMessageViewHolder(View itemView) {
            super(itemView);
            receiverName = itemView.findViewById(R.id.receiver_name);
            receiverContent = itemView.findViewById(R.id.receiver_content);
            receiverTime = itemView.findViewById(R.id.receiver_time);
            profileImage = itemView.findViewById(R.id.profile);
        }
    }
}
