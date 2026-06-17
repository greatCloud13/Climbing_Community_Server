package com.project.greatcloud13.ClimbingWith.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

//  ====================================GYM ERROR CODE=================================
    GYM_NOT_FOUND(HttpStatus.NOT_FOUND, "G001", "암장을 찾을 수 없습니다."),
    ACCESS_DENIED_GYM(HttpStatus.FORBIDDEN, "G002", "암장에 대한 권한이 없습니다."),

//  ====================================SECTOR ERROR CODE=================================
    SECTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "섹터를 찾을 수 없습니다."),
    ACCESS_DENIED_SECTOR(HttpStatus.FORBIDDEN, "S002", "섹터에 대한 권한이 없습니다."),


//  ====================================GYM LEVEL ERROR CODE=================================
    GYM_LEVEL_NOT_FOUND(HttpStatus.NOT_FOUND, "GL001", "레벨을 찾을 수 없습니다."),
    ACCESS_DENIED_GYM_LEVEL(HttpStatus.FORBIDDEN, "GL002", "레벨에 대한 권한이 없습니다."),

//  ====================================AUTH ERROR CODE=================================
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "AU001", "이미 사용중인 아이디입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "AU002", "이미 사용중인 닉네임입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "AU003", "이미 사용중인 이메일입니다."),

//  ====================================USER ERROR CODE=================================
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),


//  ====================================SETTING ERROR CODE=================================
    SETTING_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "세팅을 찾을 수 없습니다."),

//  ====================================CLEAR RECORD ERROR CODE=================================
    CLEAR_RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "CR001", "완등 기록을 찾을 수 없습니다."),
    ACCESS_DENIED_CLEAR_RECORD(HttpStatus.FORBIDDEN, "CR002", "완등 기록에 대한 권한이 없습니다."),

//  ====================================PROBLEM ERROR CODE=================================
    PROBLEM_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "문제를 찾을 수 없습니다."),

//  ====================================PROBLEM REVIEW ERROR CODE=================================
    PROBLEM_REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "PR001", "리뷰를 찾을 수 없습니다."),
    ACCESS_DENIED_REVIEW(HttpStatus.FORBIDDEN, "PR002", "리뷰에 대한 권한이 없습니다."),

//  ====================================POST ERROR CODE=================================
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "PO001", "게시글을 찾을 수 없습니다."),
    ACCESS_DENIED_POST(HttpStatus.FORBIDDEN, "PO002", "게시글에 대한 권한이 없습니다."),
    TO_MANY_REQUEST_POST(HttpStatus.TOO_MANY_REQUESTS, "PO003", "게시글 요청이 너무 많습니다."),

//  ====================================COMMON ERROR CODE=================================
    AccessDenied(HttpStatus.FORBIDDEN, "C001", "접근 권한이 없습니다."),


    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
