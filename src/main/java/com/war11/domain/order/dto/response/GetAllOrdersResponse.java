package com.war11.domain.order.dto.response;

import com.war11.domain.order.entity.Order;
import com.war11.domain.order.entity.OrderProduct;
import com.war11.domain.order.entity.enums.OrderStatus;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAllOrdersResponse {
  List<String> productNames;
  Long totalPrice;
  OrderStatus orderStatus;
}
