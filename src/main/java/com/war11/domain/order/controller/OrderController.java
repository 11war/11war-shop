package com.war11.domain.order.controller;

import com.war11.domain.order.dto.response.OrderResponse;
import com.war11.domain.order.service.OrderService;
import com.war11.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

  @PostMapping("/users/{userId}")
  public ResponseEntity<ApiResponse<OrderResponse>> createOrderApi(@PathVariable Long userId) {
    OrderResponse response = orderService.createOrder(userId);

    return ApiResponse.success(response);
  }

}
