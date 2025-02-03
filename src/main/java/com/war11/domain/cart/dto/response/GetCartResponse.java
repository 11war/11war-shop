package com.war11.domain.cart.dto.response;

import java.util.List;

public record GetCartResponse(
    List<CartProductResponse> cartProducts
) {

}
