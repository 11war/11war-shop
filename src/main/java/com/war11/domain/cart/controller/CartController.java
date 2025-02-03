package com.war11.domain.cart.controller;

import com.war11.domain.cart.dto.request.AddCartProductRequest;
import com.war11.domain.cart.dto.request.UpdateCartProductRequest;
import com.war11.domain.cart.dto.response.CartProductResponse;
import com.war11.domain.cart.dto.response.CartResponse;
import com.war11.domain.cart.dto.response.GetCartResponse;
import com.war11.domain.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {

  private final CartService cartService;

  // Todo: userId 패스배리어블 말고 토큰에서 빼도록 수정
  @PostMapping("/users/{userId}/products/{productId}")
  public ResponseEntity<Void> addToCartApi(@RequestBody AddCartProductRequest request,
      @PathVariable Long userId, @PathVariable Long productId) {
    cartService.addToCart(request, userId, productId);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  // Todo: userId 패스배리어블 말고 토큰에서 빼도록 수정
  @GetMapping("/users/{userId}")
  public ResponseEntity<GetCartResponse> getCartApi(@PathVariable Long userId) {
    GetCartResponse response = cartService.getCart(userId);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PatchMapping("/cart-products/{id}/quantity")
  public ResponseEntity<CartProductResponse> updateQuantityApi(
      @PathVariable(name = "id") Long cartProductId,
      @Valid @RequestBody UpdateCartProductRequest request) {
    CartProductResponse response = cartService.updateQuantity(cartProductId, request);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PatchMapping("/cart-products/{id}/checked")
  public ResponseEntity<CartProductResponse> toggleCheckedApi(
      @PathVariable(name = "id") Long cartProductId) {
    CartProductResponse response = cartService.toggleChecked(cartProductId);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
