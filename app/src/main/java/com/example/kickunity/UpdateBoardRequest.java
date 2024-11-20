package com.example.kickunity;

public class UpdateBoardRequest {

    private String title;
    private String content;

    // Constructor
    public UpdateBoardRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // Getter and Setter methods
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
