package com.war11.global.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(400, "1", "1"),
    RESOURCE_NOT_FOUND(404,"",""),
    INTERNAL_SERVER_ERROR(500,"",""),

    //Auth
    NOT_FOUND_TOKEN(404,"404","토큰이 유효하지 않습니다."),
    ID_ALREADY_EXISTS(409,"409","이미 해당 아이디가 존재합니다."),
    NOT_FOUND_ID(404,"", "해당 아이디가 존재하지 않습니다."),

    //User
    USER_ID_ERROR(400,"1","아이디나 비밀번호가 올바르지 않습니다."),
    USER_PW_ERROR(400,"2","아이디나 비밀번호가 올바르지 않습니다.");

    //Product

    //Cart

    //Order

    //Coupon

    private final int status;
    private final String code;
    private final String message;
}
