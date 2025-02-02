package com.war11.domain.cart.controller;

import com.war11.domain.cart.dto.response.CartResponse;
import com.war11.domain.cart.dto.response.GetCartResponse;
import com.war11.domain.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {

  private final CartService cartService;

  @PostMapping
  public ResponseEntity<CartResponse> createCartApi(){
    CartResponse response = cartService.createCart();

    return new ResponseEntity<>(response, HttpStatus.OK);
  }


  @GetMapping("/{user_id}")
  public ResponseEntity<GetCartResponse> getCartApi(@PathVariable(name = "user_id") Long userId){
    GetCartResponse response = cartService.getCart(userId);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
