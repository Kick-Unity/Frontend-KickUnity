package com.example.kickunity.Team;

public class UpdateTeamRequest {
    private String st; // 서버에서 요구하는 연령대 데이터
    private int number; // 추가적으로 요구되는 정수 값

    public UpdateTeamRequest(String st, int number) {
        this.st = st;
        this.number = number;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}