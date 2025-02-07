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
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "","이미 로그아웃 되었습니다. 로그인이 필요합니다."),
    UNAUTHORIZED_ID(HttpStatus.UNAUTHORIZED,"","권한이 없습니다. 관리자만 접근이 가능합니다."),

    //User
    USER_ID_ERROR(HttpStatus.UNAUTHORIZED,"1","아이디나 비밀번호가 올바르지 않습니다."),
    USER_PW_ERROR(HttpStatus.UNAUTHORIZED,"2","아이디나 비밀번호가 올바르지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "", "유저를 찾을 수 없습니다."),
    BAD_REQUEST_PW(HttpStatus.BAD_REQUEST, "","기존 비밀번호가 틀렸습니다. 정보가 변경되지 않습니다."),


    //Product
    NOT_FOUND_PRODUCT_ID(HttpStatus.NOT_FOUND,"","찾는 상품이 없습니다."),
    NOT_FOUND_KEYWORD_ID(HttpStatus.BAD_REQUEST,"" ,"해당하는 키워드가 없습니다." );
    INVALID_PRODUCT_STATUS_VALUE(HttpStatus.BAD_REQUEST,"" ,"해당하는 상태코드가 없습니다." ),
    OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "", "재고가 없습니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "", "재고가 부족합니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "", "상품을 찾을 수 없습니다."),

    //Cart
    CART_IS_EMPTY(HttpStatus.NOT_FOUND, "", "장바구니에 상품이 없습니다."),
    CART_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "", "장바구니 상품을 찾을 수 없습니다."),

    //Order
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "", "주문을 찾을 수 없습니다."),
    ORDER_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "", "주문 상품을 찾을 수 없습니다."),
    CANNOT_CANCEL_ORDER(HttpStatus.BAD_REQUEST, "", "배송중이거나 배송완료되어 취소할 수 없는 주문입니다."),
    ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "", "이미 취소된 주문입니다."),

    //Coupon
    COUPON_TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "", "쿠폰 템플릿을 찾을 수 없습니다."),
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "", "쿠폰을 찾을 수 없습니다."),
    ALREADY_ISSUED(HttpStatus.BAD_REQUEST,"","이미 발급된 쿠폰입니다."),
    COUPON_SOLD_OUT(HttpStatus.NOT_ACCEPTABLE,"","모든 쿠폰이 소진되었습니다."),
    COUPON_ISSUE_DENIED(HttpStatus.TOO_MANY_REQUESTS,"","요청이 너무 많습니다. 잠시 후 다시 시도하세요."),
    TIME_OUT_COUPON_ISSUE(HttpStatus.REQUEST_TIMEOUT,"","요청이 만료되었습니다. 잠시 후 다시 시도하세요."),

    //lock
    TIME_OUT_LOCK(HttpStatus.REQUEST_TIMEOUT,"","Lock 요청이 만료되었습니다."),
    DENIED_GET_LOCK(HttpStatus.NOT_ACCEPTABLE,"","Lock 획득에 실패했습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
