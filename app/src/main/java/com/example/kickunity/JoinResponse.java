package com.example.kickunity;

public class JoinResponse {
    private long memberId;  // 서버에서 반환하는 회원 ID

    // Getter와 Setter
    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }
}
