package com.war11.global.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(400, "1", "1"),
    RESOURCE_NOT_FOUND(404,"",""),
    INTERNAL_SERVER_ERROR(500,"","");
    //User

    //Product

    //Cart

    //Order

    //Coupon

    private final int status;
    private final String code;
    private final String message;
}
