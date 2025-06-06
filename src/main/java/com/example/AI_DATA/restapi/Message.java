package com.example.AI_DATA.restapi;

public enum Message {
    BULLETIN_NOT_FOUND("게시글 조회 실패"),
    BULLETIN_FOUND("게시글 조회 성공"),
    BULLETIN_SAVE_SUCCESS("게시글 저장 성공"),
    BULLETIN_SAVE_FAILED("게시글 저장 실패"),
    BULLETIN_MODIFY_SUCCESS("게시글 수정 성공"),
    BULLETIN_MODIFY_FAILED("게시글 수정 실패"),
    BULLETIN_DELETE_SUCCESS("게시글 삭제 성공"),
    BULLETIN_DELETE_FAILED("게시슬 삭제 실패"),
    BULLETIN_AI_PREDICTION_SUCCESS("인공지능 서버 결과 불러오기 성공"),
    BULLETIN_AI_PREDICTION_FAILED("인공지능 서버 결과 불러오기 실패"),
    BULLETIN_URL_EXIST("게시글 URL 검색 성공"),
    BULLETIN_URL_NOT_EXIST("게시글에 URL이 존재하지 않음");




    private final String label;

    Message(String label) {
        this.label = label;
    }

    public String label() {
        return this.label;
    }
}
