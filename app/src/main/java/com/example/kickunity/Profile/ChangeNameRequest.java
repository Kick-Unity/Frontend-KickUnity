package com.example.kickunity.Profile;

public class ChangeNameRequest {
    private String newName;

    public ChangeNameRequest(String newName) {
        this.newName = newName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}
