package com.war11.domain.cart.service;

import com.war11.domain.cart.dto.request.CartRequest;
import com.war11.domain.cart.dto.response.CartResponse;
import com.war11.domain.cart.entity.Cart;
import com.war11.domain.cart.repository.CartRepository;
import com.war11.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

  private final CartRepository cartRepository;

  // Todo: User new로 만들어서 넣지 않고 토큰에서 뽑아 넣도록 수정하기
  public CartResponse createCart() {
    Cart cart = new Cart(new User());
    cartRepository.save(cart);
    return new CartResponse("카트가 생성되었습니다.");
  }
}
