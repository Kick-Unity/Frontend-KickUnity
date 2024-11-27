package com.example.kickunity.Board;

public class AddBoardRequest {

    private String title;
    private String content;
    private String category;

    // Constructor
    public AddBoardRequest(String title, String content, String category) {
        this.title = title;
        this.content = content;
        this.category = category;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
