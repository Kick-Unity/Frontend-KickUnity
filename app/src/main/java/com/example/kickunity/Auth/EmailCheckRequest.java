package com.example.kickunity.Auth;

public class EmailCheckRequest {
    private String email;
    private String authNum;  // 인증번호는 String

    public EmailCheckRequest(String email, String authNum) {
        this.email = email;
        this.authNum = authNum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuthNum() {
        return authNum;
    }

    public void setAuthNum(String authNum) {
        this.authNum = authNum;
    }
}
