package com.war11.domain.cart.controller;

import com.war11.domain.cart.dto.request.AddCartProductRequest;
import com.war11.domain.cart.dto.request.UpdateCartProductRequest;
import com.war11.domain.cart.dto.response.CartProductResponse;
import com.war11.domain.cart.dto.response.CartResponse;
import com.war11.domain.cart.dto.response.GetCartResponse;
import com.war11.domain.cart.service.CartService;
import com.war11.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
  public ResponseEntity<ApiResponse<Void>> addToCartApi(@RequestBody AddCartProductRequest request,
      @PathVariable Long userId, @PathVariable Long productId) {
    cartService.addToCart(request, userId, productId);

    return ApiResponse.noContentAndSendMessage("장바구니에 상품이 추가되었습니다.");
  }

  // Todo: userId 패스배리어블 말고 토큰에서 빼도록 수정
  @GetMapping("/users/{id}")
  public ResponseEntity<ApiResponse<GetCartResponse>> getCartApi(@PathVariable(name = "id") Long userId) {
    GetCartResponse response = cartService.getCart(userId);

    return ApiResponse.success(response);
  }

  @PatchMapping("/cart-products/{id}/quantity")
  public ResponseEntity<ApiResponse<CartProductResponse>> updateQuantityApi(
      @PathVariable(name = "id") Long cartProductId,
      @Valid @RequestBody UpdateCartProductRequest request) {
    CartProductResponse response = cartService.updateQuantity(cartProductId, request);
    return ApiResponse.success(response);
  }

  @PatchMapping("/cart-products/{id}/checked")
  public ResponseEntity<ApiResponse<CartProductResponse>> toggleCheckedApi(
      @PathVariable(name = "id") Long cartProductId) {
    CartProductResponse response = cartService.toggleChecked(cartProductId);
    return ApiResponse.success(response);
  }

  @DeleteMapping("/cart-product/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteCartProductApi(@PathVariable(name = "id") Long cartProductId) {
    cartService.deleteCartProduct(cartProductId);

    return ApiResponse.noContentAndSendMessage("장바구니에서 상품이 삭제되었습니다.");
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteCartApi(@PathVariable(name = "id") Long id) {
    cartService.deleteCart(id);

    return ApiResponse.noContentAndSendMessage("장바구니가 비어있습니다.");
  }
}
