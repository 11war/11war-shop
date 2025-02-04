package com.war11.domain.order.service;

import com.war11.domain.cart.entity.Cart;
import com.war11.domain.cart.entity.CartProduct;
import com.war11.domain.cart.repository.CartProductRepository;
import com.war11.domain.cart.repository.CartRepository;
import com.war11.domain.order.dto.response.OrderProductResponse;
import com.war11.domain.order.dto.response.OrderResponse;
import com.war11.domain.order.entity.Order;
import com.war11.domain.order.entity.OrderProduct;
import com.war11.domain.order.repository.OrderProductRepository;
import com.war11.domain.order.repository.OrderRepository;
import com.war11.domain.product.repository.ProductRepository;
import com.war11.domain.user.entity.User;
import com.war11.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

  private final ProductRepository productRepository;
  private final CartRepository cartRepository;
  private final CartProductRepository cartProductRepository;
  private final UserRepository userRepository;
  private final OrderRepository orderRepository;
  private final OrderProductRepository orderProductRepository;

  public OrderResponse createOrder(Long userId) {
    User foundUser = userRepository.findById(userId).orElseThrow();
    Order order = orderRepository.save(new Order(foundUser));
    Cart foundCart = cartRepository.findCartByUserId(userId).orElseThrow();

    List<CartProduct> cartProducts = cartProductRepository.findCartProductByCartIdAndIsChecked(
        foundCart.getId(), true);

    List<OrderProduct> orderProducts = cartProducts.stream()
        .map(cartProduct -> new OrderProduct(order, cartProduct.getProduct().getName(),
            cartProduct.getProduct().getPrice(), cartProduct.getQuantity())).toList();

    orderProductRepository.saveAll(orderProducts);

    List<OrderProductResponse> responses = orderProducts.stream()
        .map(OrderProduct::toDto).toList();

    return order.toDto(responses);
  }
}
