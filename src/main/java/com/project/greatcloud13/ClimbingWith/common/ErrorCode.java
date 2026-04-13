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


//  ====================================USER ERROR CODE=================================
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),


//  ====================================SETTING ERROR CODE=================================
    SETTING_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "세팅을 찾을 수 없습니다."),

//  ====================================SUBSCRIBE ERROR CODE=================================
    SUBSCRIBE_NOT_FOUND(HttpStatus.NOT_FOUND, "SS001", "구독 정보를 찾을 수 없습니다."),

    
    //  ====================================COMMON ERROR CODE=================================
    NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED, "C001", "준비중인 기능입니다."),
    AccessDenied(HttpStatus.FORBIDDEN, "C002", "접근 권한이 없습니다."),


    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
