package com.example.kickunity.Profile;

// DeleteMemberRequest 클래스 정의
public class DeleteMemberRequest {

    private String password; // 회원 삭제 시 사용될 비밀번호

    // 기본 생성자
    public DeleteMemberRequest() {
    }

    // 모든 필드를 포함한 생성자
    public DeleteMemberRequest(String password) {
        this.password = password;
    }

    // Getter 메서드
    public String getPassword() {
        return password;
    }

    // Setter 메서드
    public void setPassword(String password) {
        this.password = password;
    }
}
