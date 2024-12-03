package com.example.kickunity.Chat;

import java.io.Serializable;

public class ChatRoomDTO implements Serializable {  // Serializable 추가
    private Long id;
    private MemberDTO currentUser;
    private MemberDTO otherUser;

    // Getter and Setter methods

    public Long getRoomId() {
        return id;
    }

    public void setRoomId(Long id) {
        this.id = id;
    }

    public MemberDTO getcurrentUser() {
        return currentUser;
    }

    public void setcurrentUser(MemberDTO currentUser) {
        this.currentUser = currentUser;
    }

    public MemberDTO getotherUser() {
        return otherUser;
    }

    public void setotherUser(MemberDTO otherUser) {
        this.otherUser = otherUser;
    }

    public static class MemberDTO implements Serializable {
        private Long id;
        private String name;

        // Constructor, Getter and Setter methods
        public MemberDTO(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
