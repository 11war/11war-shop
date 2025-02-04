package com.war11.domain.cart.service;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import com.war11.domain.cart.dto.request.AddCartProductRequest;
import com.war11.domain.cart.entity.Cart;
import com.war11.domain.cart.entity.CartProduct;
import com.war11.domain.cart.repository.CartProductRepository;
import com.war11.domain.cart.repository.CartRepository;
import com.war11.domain.product.entity.Product;
import com.war11.domain.product.repository.ProductRepository;
import com.war11.domain.user.entity.User;
import com.war11.domain.user.repository.UserRepository;
import java.util.List;
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

  @InjectMocks
  private CartService cartService;

  private User user;
  private Product product1;
  private Product product2;
  private Cart cart;
  private CartProduct cartProduct1;
  private CartProduct cartProduct2;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    user = mock(User.class);
    product1 = mock(Product.class);
    product2 = mock(Product.class);
    cart = mock(Cart.class);
    cartProduct1 = mock(CartProduct.class);
    cartProduct2 = mock(CartProduct.class);
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
    ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
    verify(cartRepository).save(cartCaptor.capture());
    Cart savedCart = cartCaptor.getValue();
    assertThat(savedCart.getUser().getId()).isEqualTo(1L);

    ArgumentCaptor<CartProduct> cartProductCaptor = ArgumentCaptor.forClass(CartProduct.class);
    verify(cartProductRepository).save(cartProductCaptor.capture());
    CartProduct savedCartProduct = cartProductCaptor.getValue();
    assertThat(savedCartProduct.getProduct().getId()).isEqualTo(1L);
  }

  @Test
  void 장바구니_존재할때_아이템추가() {
    // given: dto 생성, user/product/cart id 부여, 카트/프로덕트 검색결과 부여
    AddCartProductRequest request = new AddCartProductRequest(2, true);

    when(user.getId()).thenReturn(1L);
    when(product1.getId()).thenReturn(1L);
    when(cart.getId()).thenReturn(1L);

    when(cartRepository.findCartByUserId(user.getId())).thenReturn(Optional.of(cart));
    when(productRepository.findById(product1.getId())).thenReturn(Optional.of(product1));

    // when: 메서드 실행
    cartService.addToCart(request, 1L, 1L);

    // then
    // 장바구니 저장 메서드가 실행된적이 없는지(never) 확인
    verify(cartRepository, never()).save(any());

    // 새로운 상품이 장바구니에 정상적으로 추가되었는지 확인(캡쳐 객체)
    ArgumentCaptor<CartProduct> cartProductCaptor = ArgumentCaptor.forClass(CartProduct.class);
    verify(cartProductRepository).save(cartProductCaptor.capture());
    CartProduct savedCartProduct = cartProductCaptor.getValue();

    assertThat(savedCartProduct.getCart()).isEqualTo(cart);
    assertThat(savedCartProduct.getProduct()).isEqualTo(product1);
    assertThat(savedCartProduct.getQuantity()).isEqualTo(2);
    assertThat(savedCartProduct.isChecked()).isTrue();
  }

  @Test
  void 장바구니에서_마지막_상품_삭제시_장바구니도_삭제() {
    // given: cart 와 cart 의 id 를 가지고 있는 cartProduct 세팅, 카트프로덕트 검색 결과 세팅(원래 1개, 삭제 후 빈 리스트)
    when(cartProduct1.getId()).thenReturn(1L);
    when(cartProduct1.getCart()).thenReturn(cart);
    when(cart.getId()).thenReturn(1L);

    when(cartProductRepository.findById(cartProduct1.getId()))
        .thenReturn(Optional.of(cartProduct1));

    when(cartProductRepository.findCartProductByCartIdAndIsChecked(1L, true))
        .thenReturn(List.of(cartProduct1))
        .thenReturn(List.of());

    // when
    cartService.deleteCartProduct(cartProduct1.getId());

    // then
    verify(cartProductRepository).delete(cartProduct1);
    verify(cartRepository).delete(cart);
  }

}