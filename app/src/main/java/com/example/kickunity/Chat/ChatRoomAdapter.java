package com.example.kickunity.Chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kickunity.R;

import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SENDER = 0;
    private static final int TYPE_RECEIVER = 1;

    private List<ChatMessage> chatMessages;
    private Long currentUserId;

    public ChatRoomAdapter(List<ChatMessage> chatMessages, Long currentUserId) {
        this.chatMessages = chatMessages;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        // 발신자가 현재 사용자 ID와 일치하는지 확인하여 메시지 유형을 결정
        if (chatMessage.getSenderId().equals(currentUserId)) {
            return TYPE_SENDER;  // 본인이 보낸 메시지
        } else {
            return TYPE_RECEIVER;  // 다른 사용자가 보낸 메시지
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SENDER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_receiver, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);

        if (holder instanceof SenderViewHolder) {
            ((SenderViewHolder) holder).bind(chatMessage);
        } else if (holder instanceof ReceiverViewHolder) {
            ((ReceiverViewHolder) holder).bind(chatMessage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    // 발신자 ViewHolder
    public static class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderContent;
        TextView senderTime;

        public SenderViewHolder(View itemView) {
            super(itemView);
            senderContent = itemView.findViewById(R.id.sender_content);
            senderTime = itemView.findViewById(R.id.sender_time);
        }

        public void bind(ChatMessage chatMessage) {
            senderContent.setText(chatMessage.getMessage());
            senderTime.setText(chatMessage.getTime());  // 메시지 발송 시간 처리 추가
        }
    }

    // 수신자 ViewHolder
    public static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView receiverName;
        TextView receiverContent;
        TextView receiverTime;

        public ReceiverViewHolder(View itemView) {
            super(itemView);
            receiverName = itemView.findViewById(R.id.receiver_name);
            receiverContent = itemView.findViewById(R.id.receiver_content);
            receiverTime = itemView.findViewById(R.id.receiver_time);
        }

        public void bind(ChatMessage chatMessage) {
            receiverName.setText(chatMessage.getSender());  // 수신자의 이름을 표시 (또는 이메일)
            receiverContent.setText(chatMessage.getMessage());
            receiverTime.setText(chatMessage.getTime());  // 메시지 발송 시간 처리 추가
        }
    }

    // ChatMessage 모델
    public static class ChatMessage {
        private Long senderId;  // 발신자의 ID
        private String sender;  // 발신자의 이름 (혹은 이메일)
        private String message; // 메시지 내용
        private String time;    // 메시지 보낸 시간

        public ChatMessage(Long senderId, String sender, String message, String time) {
            this.senderId = senderId;
            this.sender = sender;
            this.message = message;
            this.time = time;
        }

        public Long getSenderId() {
            return senderId;
        }

        public String getSender() {
            return sender;
        }

        public String getMessage() {
            return message;
        }

        public String getTime() {
            return time;
        }
    }

    // 메시지 추가 메소드
    public void addMessage(Long senderId, String sender, String message, String time) {
        chatMessages.add(new ChatMessage(senderId, sender, message, time));
        notifyItemInserted(chatMessages.size() - 1);  // 마지막에 추가된 메시지를 RecyclerView에 반영
    }
}
