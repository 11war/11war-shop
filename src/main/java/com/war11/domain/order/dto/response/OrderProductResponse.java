package com.war11.domain.order.dto.response;

import com.war11.domain.order.entity.OrderProduct;
import lombok.Builder;

@Builder
public record OrderProductResponse(
    Long orderId,
    Long orderProductId,
    String productName,
    Long productPrice,
    Integer quantity

) {

  public OrderProduct toEntity() {
    return OrderProduct.builder()
        .productName(productName)
        .productPrice(productPrice)
        .quantity(quantity)
        .build();
  }
}
