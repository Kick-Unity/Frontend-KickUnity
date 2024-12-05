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

    private List<ChatMessageUI> chatMessages;  // List 타입을 ChatMessageUI로 변경
    private Long currentUserId;
    private RecyclerView recyclerView;  // RecyclerView 추가

    public ChatRoomAdapter(List<ChatMessageUI> chatMessages, Long currentUserId, RecyclerView recyclerView) {
        this.chatMessages = chatMessages;
        this.currentUserId = currentUserId;
        this.recyclerView = recyclerView;  // RecyclerView 초기화
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessageUI chatMessage = chatMessages.get(position);
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
        ChatMessageUI chatMessage = chatMessages.get(position);

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

        public void bind(ChatMessageUI chatMessage) {
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

        public void bind(ChatMessageUI chatMessage) {
            receiverName.setText(chatMessage.getSender());  // 수신자의 이름을 표시 (혹은 이메일)
            receiverContent.setText(chatMessage.getMessage());
            receiverTime.setText(chatMessage.getTime());  // 메시지 발송 시간 처리 추가
        }
    }

    // UI 관련 메시지 클래스 (RecyclerView에서 사용)
    public static class ChatMessageUI {
        private Long senderId;
        private String sender;
        private String message;
        private String time;

        public ChatMessageUI(Long senderId, String sender, String message, String time) {
            this.senderId = senderId;
            this.sender = sender;
            this.message = message;
            this.time = time;
        }

        // Getter 및 Setter
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

    // 이전 메시지를 RecyclerView 맨 위에 추가하는 메서드
    public void addMessagesAtStart(List<ChatMessageUI> messages) {
        chatMessages.addAll(0, messages);  // 새 메시지 목록을 기존 목록 앞에 추가
        notifyItemRangeInserted(0, messages.size());  // 새 메시지들 추가 후 RecyclerView 갱신
    }

    public void addMessage(Long senderId, String sender, String message, String time) {
        // 새로운 메시지를 추가
        chatMessages.add(new ChatMessageUI(senderId, sender, message, time));
        notifyItemInserted(chatMessages.size() - 1);  // 새로운 메시지 위치에 추가 후 갱신

        // RecyclerView의 마지막 항목으로 자동 스크롤
        recyclerView.scrollToPosition(chatMessages.size() - 1);  // 최신 메시지로 스크롤
    }

    // 메시지 목록을 업데이트하는 메서드
    public void updateMessages(List<ChatMessageUI> newMessages) {
        chatMessages.clear();  // 기존 메시지 목록을 비우고
        chatMessages.addAll(newMessages);  // 새로운 메시지 목록 추가
        notifyDataSetChanged();  // RecyclerView 갱신
    }
}
