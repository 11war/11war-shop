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
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"","");
    //User

    //Product

    //Cart

    //Order

    //Coupon

    private final HttpStatus status;
    private final String code;
    private final String message;
}
