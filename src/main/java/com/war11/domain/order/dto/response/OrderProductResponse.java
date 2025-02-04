package com.war11.domain.order.dto.response;

import com.war11.domain.order.entity.Order;
import com.war11.domain.order.entity.OrderProduct;
import com.war11.domain.product.entity.Product;
import lombok.Builder;

@Builder
public record OrderProductResponse(
    Order order,
    Product product,
    Integer quantity

) {

  public OrderProduct toEntity() {
    return OrderProduct.builder()
        .order(order)
        .product(product)
        .quantity(quantity)
        .build();
  }
}
