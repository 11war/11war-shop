package com.war11.domain.order.controller;

import com.war11.domain.order.dto.request.ChangeOrderStatusRequest;
import com.war11.domain.order.dto.response.CancelOrderResponse;
import com.war11.domain.order.dto.response.GetAllOrdersResponse;
import com.war11.domain.order.dto.response.OrderResponse;
import com.war11.domain.order.dto.response.UpdateOrderResponse;
import com.war11.domain.order.service.OrderService;
import com.war11.global.common.ApiResponse;
import com.war11.global.config.CustomUserDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

  private final OrderService orderService;

  @PostMapping
  public ResponseEntity<ApiResponse<OrderResponse>> createOrderApi(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long userId = userDetails.getId();
    Long discountPrice = 30000L;
    OrderResponse response = orderService.createOrder(userId, discountPrice);

    return ApiResponse.success(response);
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<GetAllOrdersResponse>>> getAllOrdersApi(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long userId = userDetails.getId();
    List<GetAllOrdersResponse> responses = orderService.getAllOrder(userId);

    return ApiResponse.success(responses);
  }

  @GetMapping("/{ordersId}")
  public ResponseEntity<ApiResponse<OrderResponse>> getOrderApi(
      @PathVariable Long ordersId) {
    OrderResponse response = orderService.getOrder(ordersId);

    return ApiResponse.success(response);
  }

  @PatchMapping("/{orderId}")
  public ResponseEntity<ApiResponse<UpdateOrderResponse>> updateOrderApi(@PathVariable Long orderId,
      @RequestBody ChangeOrderStatusRequest request) {
    UpdateOrderResponse response = orderService.updateOrder(orderId, request);

    return ApiResponse.success(response);
  }

  @DeleteMapping("/{orderId}")
  public ResponseEntity<ApiResponse<CancelOrderResponse>> deleteOrderApi(
      @PathVariable Long orderId) {
    CancelOrderResponse response = orderService.cancelOrder(orderId);

    return ApiResponse.success(response);
  }
}
