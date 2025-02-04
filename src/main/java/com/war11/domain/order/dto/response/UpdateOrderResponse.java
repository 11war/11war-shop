package com.war11.domain.order.dto.response;

import com.war11.domain.order.entity.enums.OrderStatus;

public record UpdateOrderResponse(
    String message,
    OrderStatus status
) {

}
