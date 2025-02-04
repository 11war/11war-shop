package com.war11.domain.order.dto.response;

import com.war11.domain.order.entity.Order;
import com.war11.domain.order.entity.OrderProduct;
import com.war11.domain.order.entity.enums.OrderStatus;
import com.war11.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record OrderResponse (
    User user,
    List<OrderProduct> orderProducts,
    Long discountedPrice,
    Long totalPrice,
    OrderStatus orderStatus,
    LocalDateTime createdAt,
    LocalDateTime updatedAt

) {

  public Order toEntity() {
    return Order.builder()
        .user(user)
        .discountedPrice(discountedPrice)
        .totalPrice(totalPrice)
        .status(orderStatus)
        .build();
  }
}