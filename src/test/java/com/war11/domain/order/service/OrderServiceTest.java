package com.war11.domain.order.service;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import com.war11.domain.cart.entity.Cart;
import com.war11.domain.cart.entity.CartProduct;
import com.war11.domain.cart.repository.CartProductRepository;
import com.war11.domain.cart.repository.CartRepository;
import com.war11.domain.order.dto.response.OrderResponse;
import com.war11.domain.order.entity.Order;
import com.war11.domain.order.entity.OrderProduct;
import com.war11.domain.order.repository.OrderProductRepository;
import com.war11.domain.order.repository.OrderRepository;
import com.war11.domain.product.entity.Product;
import com.war11.domain.user.entity.User;
import com.war11.domain.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class OrderServiceTest {

  @Mock
  private CartRepository cartRepository;
  @Mock
  private CartProductRepository cartProductRepository;
  @Mock
  private Product product;
  @Mock
  private UserRepository userRepository;
  @Mock
  private OrderRepository orderRepository;
  @Mock
  private OrderProductRepository orderProductRepository;

  @InjectMocks
  private OrderService orderService;

  private User user;
  private Product product1;
  private Product product2;
  private Cart cart;
  private CartProduct cartProduct1;
  private CartProduct cartProduct2;
  private Order order1;
  private Order order2;
  private OrderProduct orderProduct1;
  private OrderProduct orderProduct2;
  private OrderProduct orderProduct3;
  private OrderProduct orderProduct4;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    user = mock(User.class);
    product1 = mock(Product.class);
    product2 = mock(Product.class);
    cart = mock(Cart.class);
    cartProduct1 = mock(CartProduct.class);
    cartProduct2 = mock(CartProduct.class);
    order1 = mock(Order.class);
    order2 = mock(Order.class);
    orderProduct1 = mock(OrderProduct.class);
    orderProduct2 = mock(OrderProduct.class);
    orderProduct3 = mock(OrderProduct.class);
    orderProduct4 = mock(OrderProduct.class);
  }

  @Test
  void 주문_생성_성공() {
    // given: 유저, 장바구니, 상품 설정
    when(user.getId()).thenReturn(101L);
    when(userRepository.findById(101L)).thenReturn(Optional.of(user));

    when(cartRepository.findCartByUserId(101L)).thenReturn(Optional.of(cart));
    when(cart.getId()).thenReturn(201L);

    when(product1.getId()).thenReturn(301L);
    when(product1.getPrice()).thenReturn(5000L);
    when(product2.getId()).thenReturn(302L);
    when(product2.getPrice()).thenReturn(3000L);

    when(cartProduct1.getCart()).thenReturn(cart);
    when(cartProduct1.getProduct()).thenReturn(product1);
    when(cartProduct1.getQuantity()).thenReturn(2);

    when(cartProduct2.getCart()).thenReturn(cart);
    when(cartProduct2.getProduct()).thenReturn(product2);
    when(cartProduct2.getQuantity()).thenReturn(1);

    when(cartProductRepository.findCartProductByCartIdAndIsChecked(201L, true))
        .thenReturn(List.of(cartProduct1, cartProduct2));

    when(order1.getId()).thenReturn(401L);
    when(order1.getUser()).thenReturn(user);
    when(orderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    when(orderProductRepository.saveAll(any()))
        .thenReturn(List.of(orderProduct1, orderProduct2));

    // when
    OrderResponse response = orderService.createOrder(101L, 1000L);

    // then
    assertThat(response.user()).isEqualTo(user);
    assertThat(response.orderProducts()).hasSize(2);
    assertThat(response.resultPrice()).isEqualTo(5000L * 2 + 3000L - 1000L);
    assertThat(response.orderStatus()).isNotNull();

    // 주문 저장 검증
    verify(orderRepository).save(any());
    verify(orderProductRepository).saveAll(any());

    // 재고 감소 확인
    verify(product1).downToQuantity(2);
    verify(product2).downToQuantity(1);
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