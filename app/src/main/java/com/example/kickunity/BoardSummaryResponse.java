package com.example.kickunity;

public class BoardSummaryResponse {

    private Long id;         // 게시글 ID
    private String title;    // 게시글 제목
    private String content;  // 게시글 내용 (요약)
    private String createdDate; // yyyy-mm-dd HH:mm:ss

    // Getter and Setter for each field
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
    public String getcreatedDate() {
        return createdDate;
    }

    public void setcreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

}
