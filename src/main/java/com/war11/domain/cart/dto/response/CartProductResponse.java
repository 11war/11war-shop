package com.war11.domain.cart.dto.response;

import lombok.Builder;

@Builder
public record CartProductResponse(
    String productName,
    Long productPrice,
    Integer productQuantity,
    boolean isChecked
) {

}
