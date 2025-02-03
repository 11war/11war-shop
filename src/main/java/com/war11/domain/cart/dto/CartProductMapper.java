package com.war11.domain.cart.dto;

import com.war11.domain.cart.dto.response.CartProductResponse;
import com.war11.domain.cart.entity.CartProduct;
import org.springframework.stereotype.Component;

@Component
public class CartProductMapper {
  public CartProductResponse toDto(CartProduct cartProduct) {
    return CartProductResponse.builder()
        .productName(cartProduct.getProduct().getName())
        .productPrice(cartProduct.getProduct().getPrice())
        .productQuantity(cartProduct.getQuantity())
        .isChecked(cartProduct.isChecked())
        .build();
  }
}
