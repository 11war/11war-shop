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
    //User

    //Product
    INVALID_PRODUCT_STATUS_VALUE(400,"","상품의 상태값이 올바르지 않습니다.");

    //Cart

    //Order

    //Coupon

    private final int status;
    private final String code;
    private final String message;
}
