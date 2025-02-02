package com.war11.domain.cart.dto.response;

import com.war11.domain.cart.entity.CartProduct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetCartProductResponse {

  private Long id;
  private Long cartId;
  private Long productId;
  private Integer quantity;
  private boolean isChecked;

  public static GetCartProductResponse toDto(CartProduct cartProduct) {
    return new GetCartProductResponse(
        cartProduct.getId(),
        cartProduct.getCart().getId(),
        cartProduct.getProduct().getId(),
        cartProduct.getQuantity(),
        cartProduct.isChecked()
    );
  }
}
