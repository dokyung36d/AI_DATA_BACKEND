package com.example.AI_DATA.restapi;

public enum Message {
    BULLETIN_NOT_FOUND("게시글 조회 실패"),
    BULLETIN_FOUND("게시글 조회 성공");

    private final String label;

    Message(String label) {
        this.label = label;
    }

    public String label() {
        return this.label;
    }
}
