package com.example.kickunity.Board;

public class UpdateBoardRequest {
    private String title;
    private String content;
    private String category;

    public UpdateBoardRequest(String title, String content, String category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }

    // Getter와 Setter 추가
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
