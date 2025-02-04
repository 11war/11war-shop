package com.war11.domain.order.controller;

import com.war11.domain.order.dto.response.GetAllOrdersResponse;
import com.war11.domain.order.dto.response.OrderResponse;
import com.war11.domain.order.service.OrderService;
import com.war11.global.common.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

  private final OrderService orderService;

  //Todo userId는 토큰에서 받아올 거임.
  @PostMapping("/users/{userId}")
  public ResponseEntity<ApiResponse<OrderResponse>> createOrderApi(@PathVariable Long userId) {
    Long discountPrice = 30000L;
    OrderResponse response = orderService.createOrder(userId, discountPrice);

    return ApiResponse.success(response);
  }

  //Todo userId는 토큰에서 받아올 거임.
  @GetMapping("/users/{userId}")
  public ResponseEntity<ApiResponse<List<GetAllOrdersResponse>>> getAllOrdersApi(
      @PathVariable Long userId) {
    List<GetAllOrdersResponse> responses = orderService.getAllOrders(userId);

    return ApiResponse.success(responses);
  }

  //Todo userId는 토큰에서 받아올 거임.
  @GetMapping("/{ordersId}/users/{userId}")
  public ResponseEntity<ApiResponse<OrderResponse>> getOrderApi(
      @PathVariable Long userId,@PathVariable Long ordersId) {
    OrderResponse response = orderService.getOrder(userId,ordersId);

    return ApiResponse.success(response);
  }
}
