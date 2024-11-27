package com.example.kickunity.Board;

public class BoardDetailResponse {

    private Long id;          // 게시글 ID
    private String title;     // 게시글 제목
    private String content;   // 게시글 내용
    private String authorName; // 작성자 이름
    private String category;   // 게시판 카테고리
    private String createdDate;  // 게시글 작성 일자

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

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
