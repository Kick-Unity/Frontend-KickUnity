package com.example.kickunity.Board;

public class SearchBoardRequest {

    private String category;
    private String keyword;

    // Constructor
    public SearchBoardRequest(String category, String keyword) {
        this.category = category;
        this.keyword = keyword;
    }

    // Getter and Setter methods
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
