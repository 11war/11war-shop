package com.war11.domain.order.dto.response;

import com.war11.domain.order.entity.Order;
import com.war11.domain.order.entity.enums.OrderStatus;
import com.war11.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record OrderResponse (
    List<OrderProductResponse> orderProducts,
    Long totalPrice,
    Long discountedPrice,
    Long resultPrice,
    OrderStatus orderStatus,
    LocalDateTime createdAt,
    LocalDateTime updatedAt

) {

  public Order toEntity() {
    return Order.builder()
        .totalPrice(totalPrice)
        .discountedPrice(discountedPrice)
        .status(orderStatus)
        .build();
  }
}