package com.war11.domain.cart.service;

import com.war11.domain.cart.dto.CartProductMapper;
import com.war11.domain.cart.dto.request.AddCartProductRequest;
import com.war11.domain.cart.dto.request.UpdateCartProductRequest;
import com.war11.domain.cart.dto.response.CartProductResponse;
import com.war11.domain.cart.dto.response.CartResponse;
import com.war11.domain.cart.dto.response.GetCartResponse;
import com.war11.domain.cart.entity.Cart;
import com.war11.domain.cart.entity.CartProduct;
import com.war11.domain.cart.repository.CartProductRepository;
import com.war11.domain.cart.repository.CartRepository;
import com.war11.domain.product.entity.Product;
import com.war11.domain.product.repository.ProductRepository;
import com.war11.domain.user.repository.UserRepository;
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
  private final CartProductMapper cartProductMapper;

  // Todo: userId 토큰에서 뽑아서 받아오도록 수정하자.
  public CartResponse createCart(Long userId) {
    Cart cart = new Cart(userRepository.findById(userId).orElseThrow());

    cartRepository.save(cart);

    return new CartResponse("카트가 생성되었습니다.");
  }

  // Todo: userId 토큰에서 뽑아서 받아오도록 수정하자.
  public void addToCart(AddCartProductRequest request, Long userId, Long productId) {
    Cart foundCart = cartRepository.findCartByUserId(userId)
        .orElseGet(() -> new Cart(userRepository.findById(userId).orElseThrow()));

    Product foundProduct = productRepository.findById(productId).orElseThrow();

    CartProduct cartProduct = new CartProduct(foundCart, foundProduct,
        request.getQuantity(), request.isChecked());
    cartProductRepository.save(cartProduct);
  }

  // Todo: userId 토큰에서 뽑아서 받아오도록 수정하자.
  public GetCartResponse getCart(Long userId) {
    Cart foundCart = cartRepository.findCartByUserId(userId).orElseThrow();

    List<CartProductResponse> foundCartProducts = cartProductRepository.findCartProductByCartId(
        foundCart.getId()).stream().map(cartProductMapper::toDto).toList();

    return new GetCartResponse(foundCartProducts);
  }

  public CartProductResponse updateQuantity(Long cartProductId, UpdateCartProductRequest request) {
    CartProduct foundCartProduct = cartProductRepository.findById(cartProductId).orElseThrow();

    foundCartProduct.updateQuantity(request.getQuantity());
    cartProductRepository.save(foundCartProduct);

    return cartProductMapper.toDto(foundCartProduct);
  }

  public CartProductResponse toggleChecked(Long cartProductId) {
    CartProduct foundCartProduct = cartProductRepository.findById(cartProductId).orElseThrow();

    foundCartProduct.toggleCheck();
    cartProductRepository.save(foundCartProduct);

    return cartProductMapper.toDto(foundCartProduct);
  }
}
