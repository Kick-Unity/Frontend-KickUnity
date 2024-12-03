package com.example.kickunity.Chat;

import com.example.kickunity.Chat.MessageType;

public class ChatMessage {
    private Long chatRoomId;
    private String message;
    private MessageType messageType;
    private Long senderId;  // 추가된 필드 (현재 사용자 ID)

    // Getter 및 Setter
    public Long getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
}
