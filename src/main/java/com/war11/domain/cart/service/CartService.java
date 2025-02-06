package com.war11.domain.cart.service;

import com.war11.domain.cart.dto.request.AddCartProductRequest;
import com.war11.domain.cart.dto.request.UpdateCartProductRequest;
import com.war11.domain.cart.dto.response.CartProductResponse;
import com.war11.domain.cart.dto.response.GetCartResponse;
import com.war11.domain.cart.entity.Cart;
import com.war11.domain.cart.entity.CartProduct;
import com.war11.domain.cart.repository.CartProductRepository;
import com.war11.domain.cart.repository.CartRepository;
import com.war11.domain.product.entity.Product;
import com.war11.domain.product.repository.ProductRepository;
import com.war11.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

  private final CartRepository cartRepository;
  private final ProductRepository productRepository;
  private final CartProductRepository cartProductRepository;
  private final UserRepository userRepository;


  /**
   * 장바구니에 상품 추가(장바구니 없을 경우 장바구니 생성하고 추가) <br>
   * 1. {@code request}와 {@code userId},{@code productId}를 전달받는다. <br>
   * 2. {@code userId}로 카트 검색. 없으면 카트를 만들고, 있으면 카트 불러온다. <br>
   * 3. {@code productId}로 상품 찾고, 상품과 카트 이용해서 {@code cartProduct} 만들고 저장.<br>
   * ref. 반환값이 없어서 request에서 toDto메서드 사용.
   */
  public void addToCart(AddCartProductRequest request, Long userId, Long productId) {
    Cart foundCart = cartRepository.findCartByUserId(userId)
        .orElseGet(() -> {
          Cart newCart = new Cart(userRepository.findById(userId).orElseThrow());
          return cartRepository.save(newCart);
        });

    Product foundProduct = productRepository.findById(productId).orElseThrow();

    CartProduct cartProduct = request.toEntity(foundCart, foundProduct);
    cartProductRepository.save(cartProduct);
  }

  /**
   * 장바구니 조회 <br>
   * 1. {@code userId}값 받기. <br>
   * 2. {@code userId}로 카트 조회. <br>
   * 3. 조회한 카트 아이디로 장바구니 상품 전체 조회, stream으로 dto에 매핑한다. <br>
   * 4. {@code cartId}, {@code foundCartProducts}리스트로 {@code GetCarResponse}생성 후 반환
   */
  public GetCartResponse getCart(Long userId) {
    Cart foundCart = cartRepository.findCartByUserId(userId).orElseThrow();

    List<CartProductResponse> foundCartProducts = cartProductRepository.findCartProductByCartId(
        foundCart.getId()).stream().map(CartProduct::toDto).toList();

    return new GetCartResponse(foundCart.getId(), foundCartProducts);
  }

  @Transactional
  public CartProductResponse updateQuantity(Long cartProductId, UpdateCartProductRequest request) {
    CartProduct foundCartProduct = cartProductRepository.findById(cartProductId).orElseThrow();

    foundCartProduct.updateQuantity(request.getQuantity());

    return foundCartProduct.toDto();
  }

  @Transactional
  public CartProductResponse toggleChecked(Long cartProductId) {
    CartProduct foundCartProduct = cartProductRepository.findById(cartProductId).orElseThrow();

    foundCartProduct.toggleCheck();

    return foundCartProduct.toDto();
  }

  @Transactional
  public void deleteCartProduct(Long cartProductId) {
    CartProduct foundCartProduct = cartProductRepository.findById(cartProductId).orElseThrow();
    Long cartId = foundCartProduct.getCart().getId();
    cartProductRepository.delete(foundCartProduct);

    if (cartProductRepository.findCartProductByCartId(cartId).isEmpty()) {
      deleteCart(cartId);
    }
  }

  @Transactional
  public void deleteCart(Long cartId) {
    Cart foundCart = cartRepository.findById(cartId).orElseThrow();
    cartRepository.delete(foundCart);
  }
}
