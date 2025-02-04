package com.war11.domain.order.dto.request;

import com.war11.domain.order.entity.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeOrderStatusRequest(
    @NotNull
    OrderStatus orderStatus
){

}
