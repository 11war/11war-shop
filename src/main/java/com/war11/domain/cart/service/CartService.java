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
import com.war11.global.exception.BusinessException;
import com.war11.global.exception.base.NotFoundException;
import com.war11.global.exception.enums.ErrorCode;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
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
    Product foundProduct = productRepository.findById(productId).orElseThrow(
        () -> new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND)
    );
    Cart foundCart = validateCartAndCheckStock(userId, request, foundProduct);

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
    Cart foundCart = cartRepository.findCartByUserId(userId).orElseThrow(
        () -> new NotFoundException(ErrorCode.CART_NOT_FOUND)
    );

    List<CartProductResponse> foundCartProducts = cartProductRepository.findCartProductByCartId(
        foundCart.getId()).stream().map(CartProduct::toDto).toList();

    return new GetCartResponse(foundCart.getId(), foundCartProducts);
  }

  @Transactional
  public CartProductResponse updateQuantity(Long cartProductId, UpdateCartProductRequest request) {
    CartProduct foundCartProduct = findEntity(cartProductRepository, cartProductId,
        ErrorCode.PRODUCT_NOT_FOUND);

    foundCartProduct.updateQuantity(request.getQuantity());

    return foundCartProduct.toDto();
  }

  @Transactional
  public CartProductResponse toggleChecked(Long cartProductId) {
    CartProduct foundCartProduct = findEntity(cartProductRepository, cartProductId,
        ErrorCode.PRODUCT_NOT_FOUND);

    foundCartProduct.toggleCheck();

    return foundCartProduct.toDto();
  }

  @Transactional
  public void deleteCartProduct(Long cartProductId) {
    CartProduct foundCartProduct = findEntity(cartProductRepository, cartProductId,
        ErrorCode.PRODUCT_NOT_FOUND);
    Long cartId = foundCartProduct.getCart().getId();

    cartProductRepository.delete(foundCartProduct);

    if (!cartProductRepository.existsByCartId(cartId)) {
      deleteCart(cartId);
    }
  }

  @Transactional
  public void deleteCart(Long cartId) {
    Cart foundCart = findEntity(cartRepository, cartId, ErrorCode.CART_NOT_FOUND);
    cartRepository.delete(foundCart);
  }

  /**
   * 카트 존재유무, 상품 재고 검증 메서드 <br>
   * 1. {@code userId}, {@code request}, {@code product} 받음. <br>
   * 2. {@code request}와 {@code product}에서 재고 비교. 현재 재고보다 많은 수량 요청 시 예외 발생 <br>
   * 3. {@code userId}로 카트 찾고 반환. 없으면 생성 후 저장해서 반환. <br>
   */
  public Cart validateCartAndCheckStock(
      Long userId, AddCartProductRequest request, Product product) {

    if (request.getQuantity() > product.getQuantity()) {
      throw new BusinessException(ErrorCode.OUT_OF_STOCK);
    }

    Cart foundCart = cartRepository.findCartByUserId(userId)
        .orElseGet(() -> {
          Cart newCart = new Cart(userRepository.findById(userId).orElseThrow(
              () -> new NotFoundException(ErrorCode.CART_NOT_FOUND)
          ));
          return cartRepository.save(newCart);
        });
    return foundCart;
  }

  /**
   * 제네릭타입 find 메서드 <br>
   * 타입 별로 {@code repository}, {@code id}, {@code errorCode}대입받음. <br>
   * 각 레포지토리에서 {@code id}기준으로 탐색, 없을 시 예외 발생. <br>
   */
  private <T> T findEntity(JpaRepository<T, Long> repository, Long id, ErrorCode errorCode) {
    return repository.findById(id).orElseThrow(
        () -> new NotFoundException(errorCode)
    );
  }
}
