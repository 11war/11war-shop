package com.war11.global.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "1", "1"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND,"",""),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"",""),

    //Auth
    NOT_FOUND_TOKEN(HttpStatus.NOT_FOUND,"","토큰이 유효하지 않습니다."),
    ID_ALREADY_EXISTS(HttpStatus.CONFLICT,"","이미 해당 아이디가 존재합니다."),
    NOT_FOUND_ID(HttpStatus.BAD_REQUEST,"", "해당 아이디가 존재하지 않습니다."),


    //User
    USER_ID_ERROR(HttpStatus.UNAUTHORIZED,"1","아이디나 비밀번호가 올바르지 않습니다."),
    USER_PW_ERROR(HttpStatus.UNAUTHORIZED,"2","아이디나 비밀번호가 올바르지 않습니다."),


    //Product
    NOT_FOUND_PRODUCT_ID(HttpStatus.NOT_FOUND,"","찾는 상품이 없습니다."),
    INVALID_PRODUCT_STATUS_VALUE(HttpStatus.BAD_REQUEST,"" ,"해당하는 상태코드가 없습니다." );

    //Cart

    //Order

    //Coupon

    private final HttpStatus status;
    private final String code;
    private final String message;
}
