package com.war11.domain.order.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.war11.domain.cart.entity.Cart;
import com.war11.domain.cart.entity.CartProduct;
import com.war11.domain.cart.repository.CartRepository;
import com.war11.domain.order.entity.Order;
import com.war11.domain.order.repository.OrderProductRepository;
import com.war11.domain.order.repository.OrderRepository;
import com.war11.domain.product.entity.Product;
import com.war11.domain.user.entity.User;
import com.war11.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class OrderServiceTest {

  @Mock
  private CartRepository cartRepository;
  @Mock
  private CartRepository cartRepositorySpy;
  @Mock
  private UserRepository userRepository;
  @Mock
  private OrderRepository orderRepository;
  @Mock
  private OrderProductRepository orderProductRepository;

  @InjectMocks
  private OrderService orderService;

  private Cart cart;
  private CartProduct cartProduct;
  private User user;
  private Product product;
  private Order order;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    cart = mock(Cart.class);
    cartProduct = mock(CartProduct.class);
    user = mock(User.class);
    product = mock(Product.class);
    order = mock(Order.class);
  }

  @Test
  void 주문_생성_성공() {
    // given
    Long userId = 1L;
    Long discountPrice = 500L;
    when(userRepository.findById(userId).orElseThrow()).thenReturn(user);
    when(orderRepository.save(new Order(user))).thenReturn(order);
    when(cartRepository.findCartByUserId(userId).orElseThrow()).thenReturn(cart);

    // when

    // then
  }

  @Test
  void getAllOrder() {
  }

  @Test
  void getOrder() {
  }

  @Test
  void updateOrder() {
  }

  @Test
  void cancelOrder() {
  }
}