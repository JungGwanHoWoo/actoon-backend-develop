package com.actoon.actoon.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {


    BAD_REQUEST(400, "잘못된 요청입니다"),
    // 이메일, 닉네임 중복
    DUPLICATE_EMAIL(400,  "이미 존재하는 이메일입니다."),
    DUPLICATE_NICKNAME(400, "이미 존재하는 닉네임입니다."),
    REQUEST_EXPIRED(400, "만료된 요청입니다."),

    // 토큰 에러
    INVALID_TOKEN(403,  "유효하지 않은 토큰입니다."),

    // 토큰
    REF_TOKEN_EXPIRED(401,  "만료된 refresh token입니다. 로그인을 다시 해주세요."),
    ACC_TOKEN_EXPIRED(401, "만료된 access token입니다."),

    // 로그인 에러
    INVALID_INPUT(403,"이메일 또는 패스워드가 틀렸습니다."),

    // 접근 에러
    PERMISSION_DENIED(403, "접근이 거부되었습니다."),

    // 404 에러
    POST_RESOUCE_NOT_FOUND(404, "요청한 리소스 정보가 없습니다."),

    // 서버 에러
    INTERNAL_ERROR(500, "내부 서버 에러입니다.");

    //SQL_ERROR(5001, "내부 서버 에러입니다.");
    //admin


    private final String code;
    private final String message;
    private int status;

    ErrorCode(final int status, final String message) {
        this.status = status;
        this.message = message;
        this.code = message;
    }
}