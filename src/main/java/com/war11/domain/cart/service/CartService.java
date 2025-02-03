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

  // Todo: userId 토큰에서 뽑아서 받아오도록 수정하자.
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

  // Todo: userId 토큰에서 뽑아서 받아오도록 수정하자.
  public GetCartResponse getCart(Long userId) {
    Cart foundCart = cartRepository.findCartByUserId(userId).orElseThrow();

    List<CartProductResponse> foundCartProducts = cartProductRepository.findCartProductByCartId(
        foundCart.getId()).stream().map(CartProduct::toDto).toList();

    return new GetCartResponse(foundCartProducts);
  }

  public CartProductResponse updateQuantity(Long cartProductId, UpdateCartProductRequest request) {
    CartProduct foundCartProduct = cartProductRepository.findById(cartProductId).orElseThrow();

    foundCartProduct.updateQuantity(request.getQuantity());
    cartProductRepository.save(foundCartProduct);

    return foundCartProduct.toDto();
  }

  public CartProductResponse toggleChecked(Long cartProductId) {
    CartProduct foundCartProduct = cartProductRepository.findById(cartProductId).orElseThrow();

    foundCartProduct.toggleCheck();
    cartProductRepository.save(foundCartProduct);

    return foundCartProduct.toDto();
  }
}
