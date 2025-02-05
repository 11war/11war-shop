package com.war11.domain.order.service;

import com.war11.domain.cart.entity.Cart;
import com.war11.domain.cart.entity.CartProduct;
import com.war11.domain.cart.repository.CartProductRepository;
import com.war11.domain.cart.repository.CartRepository;
import com.war11.domain.coupon.annotation.Lock;
import com.war11.domain.order.dto.request.ChangeOrderStatusRequest;
import com.war11.domain.order.dto.response.CancelOrderResponse;
import com.war11.domain.order.dto.response.UpdateOrderResponse;
import com.war11.domain.order.dto.response.GetAllOrdersResponse;
import com.war11.domain.order.dto.response.OrderProductResponse;
import com.war11.domain.order.dto.response.OrderResponse;
import com.war11.domain.order.entity.Order;
import com.war11.domain.order.entity.OrderProduct;
import com.war11.domain.order.repository.OrderProductRepository;
import com.war11.domain.order.repository.OrderRepository;
import com.war11.domain.product.entity.Product;
import com.war11.domain.product.repository.ProductRepository;
import com.war11.domain.user.entity.User;
import com.war11.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

  private final CartRepository cartRepository;
  private final CartProductRepository cartProductRepository;
  private final UserRepository userRepository;
  private final OrderRepository orderRepository;
  private final OrderProductRepository orderProductRepository;
  private final ProductRepository productRepository;

  @Lock
  @Transactional
  public OrderResponse createOrder(Long userId, Long discountPrice) {
    User foundUser = userRepository.findById(userId).orElseThrow();
    Cart foundCart = cartRepository.findCartByUserId(userId).orElseThrow();

    List<CartProduct> cartProducts = cartProductRepository.findCartProductByCartIdAndIsChecked(
        foundCart.getId(), true);

    cartProducts.forEach(cartProduct -> {
      cartProduct.getProduct().downToQuantity(cartProduct.getQuantity());
    });

    Order order = new Order(foundUser);

    List<OrderProduct> orderProducts = cartProducts.stream()
        .map(cartProduct -> new OrderProduct(order, cartProduct.getProduct().getId(),
            cartProduct.getProduct().getName(), cartProduct.getProduct().getPrice(),
            cartProduct.getQuantity())).toList();

    orderProductRepository.saveAll(orderProducts);

    List<OrderProductResponse> orderProductResponses = orderProducts.stream()
        .map(OrderProduct::toDto).toList();

    order.updateOrderDetails(discountPrice, orderProducts);
    orderRepository.save(order);
    OrderResponse response = order.toDto(orderProductResponses);

    cartProductRepository.deleteAll(cartProducts);

    if (cartProductRepository.findCartProductByCartId(foundCart.getId()).isEmpty()) {
      cartRepository.delete(foundCart);
    }

    return response;
  }

  public List<GetAllOrdersResponse> getAllOrder(Long userId) {
    List<Order> orders = orderRepository.findByUserId(userId);

    return orders.stream()
        .map(order -> {
          List<OrderProduct> orderProducts = orderProductRepository.findByOrderId(order.getId());
          return GetAllOrdersResponse.builder()
              .productNames(orderProducts.stream()
                  .map(OrderProduct::getProductName)
                  .toList())
              .totalPrice(order.getTotalPrice())
              .orderStatus(order.getStatus())
              .build();
        })
        .toList();
  }

  public OrderResponse getOrder(Long orderId) {
    Order order = orderRepository.findById(orderId).orElseThrow();
    List<OrderProduct> orderProducts = orderProductRepository.findByOrderId(orderId);

    List<OrderProductResponse> orderProductResponses = orderProducts.stream()
        .map(OrderProduct::toDto)
        .toList();

    return order.toDto(orderProductResponses);
  }

  @Transactional
  public UpdateOrderResponse updateOrder(Long orderId, ChangeOrderStatusRequest request) {
    Order order = orderRepository.findById(orderId).orElseThrow();
    order.updateOrderStatus(request.orderStatus());

    return new UpdateOrderResponse("배송 상태가 변경되었습니다.", order.getStatus());
  }

  @Lock
  @Transactional
  public CancelOrderResponse cancelOrder(Long orderId) {
    Order order = orderRepository.findById(orderId).orElseThrow();
    order.cancelOrder();

    List<OrderProduct> orderProducts = orderProductRepository.findByOrderId(orderId);
    orderProducts.forEach(orderProduct -> {
      Product foundProduct = productRepository.findById(orderProduct.getProductId()).orElseThrow();
      foundProduct.upToQuantity(orderProduct.getQuantity());
    });

    return new CancelOrderResponse("주문이 취소되었습니다.", order.getStatus());
  }
}
