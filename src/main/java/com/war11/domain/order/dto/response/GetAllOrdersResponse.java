package com.war11.domain.order.dto.response;

import com.war11.domain.order.entity.enums.OrderStatus;
import java.util.List;
import lombok.Builder;

@Builder
public record GetAllOrdersResponse(
    Long orderId,
    List<String> productNames,
    Long totalPrice,
    OrderStatus orderStatus
) {

}
