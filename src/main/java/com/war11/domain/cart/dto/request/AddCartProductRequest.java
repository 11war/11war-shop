package com.war11.domain.cart.dto.request;

import com.war11.domain.cart.entity.Cart;
import com.war11.domain.cart.entity.CartProduct;
import com.war11.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddCartProductRequest {
  Integer quantity;
  boolean isChecked;

  public CartProduct toEntity (Cart cart, Product product) {
    return CartProduct.builder()
        .cart(cart)
        .product(product)
        .quantity(quantity)
        .isChecked(isChecked)
        .build();
  }
}
