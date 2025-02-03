package com.war11.domain.cart.service;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import com.war11.domain.cart.dto.CartProductMapper;
import com.war11.domain.cart.dto.request.AddCartProductRequest;
import com.war11.domain.cart.entity.Cart;
import com.war11.domain.cart.entity.CartProduct;
import com.war11.domain.cart.repository.CartProductRepository;
import com.war11.domain.cart.repository.CartRepository;
import com.war11.domain.product.entity.Product;
import com.war11.domain.product.repository.ProductRepository;
import com.war11.domain.user.entity.User;
import com.war11.domain.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CartServiceTest {


  @Mock
  private CartRepository cartRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private CartProductRepository cartProductRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private CartProductMapper cartProductMapper;

  @InjectMocks
  private CartService cartService;

  private User user;
  private Product product1;
  private Product product2;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    user = mock(User.class);
    product1 = mock(Product.class);
    product2 = mock(Product.class);
  }


  @Test
  void 장바구니_없을때_생성_및_아이템추가(){
    // given
    AddCartProductRequest request = new AddCartProductRequest(3, true);

    when(user.getId()).thenReturn(1L);
    when(product1.getId()).thenReturn(1L);

    when(cartRepository.findCartByUserId(user.getId())).thenReturn(Optional.empty());
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

    // when
    cartService.addToCart(request, 1L, 1L);

    // then
    // 저장된 카트의 유저가 1L 유저와 같은지 확인
    ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
    verify(cartRepository).save(cartCaptor.capture());
    Cart savedCart = cartCaptor.getValue();
    assertThat(savedCart.getUser().getId()).isEqualTo(1L);

    ArgumentCaptor<CartProduct> cartProductCaptor = ArgumentCaptor.forClass(CartProduct.class);
    verify(cartProductRepository).save(cartProductCaptor.capture());
    CartProduct savedCartProduct = cartProductCaptor.getValue();
    assertThat(savedCartProduct.getProduct().getId()).isEqualTo(1L);
  }
}