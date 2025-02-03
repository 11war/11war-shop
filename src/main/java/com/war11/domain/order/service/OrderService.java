package com.war11.domain.order.service;

import com.war11.domain.cart.repository.CartProductRepository;
import com.war11.domain.cart.repository.CartRepository;
import com.war11.domain.order.repository.OrderProductRepository;
import com.war11.domain.order.repository.OrderRepository;
import com.war11.domain.product.repository.ProductRepository;
import com.war11.domain.user.repository.UserRepository;
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

}
