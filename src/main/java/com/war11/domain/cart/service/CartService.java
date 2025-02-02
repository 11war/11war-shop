package com.war11.domain.cart.service;

import com.war11.domain.cart.dto.response.CartResponse;
import com.war11.domain.cart.dto.response.GetCartProductResponse;
import com.war11.domain.cart.dto.response.GetCartResponse;
import com.war11.domain.cart.entity.Cart;
import com.war11.domain.cart.repository.CartProductRepository;
import com.war11.domain.cart.repository.CartRepository;
import com.war11.domain.user.entity.User;
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
  private final UserRepository userRepository;
  private final CartProductRepository cartProductRepository;

  // Todo: User new로 만들어서 넣지 않고 토큰에서 뽑아 넣도록 수정하기
  public CartResponse createCart() {
    Cart cart = new Cart(new User());
    cartRepository.save(cart);
    return new CartResponse("카트가 생성되었습니다.");
  }

  public GetCartResponse getCart(Long userId){
    Cart foundCart = cartRepository.findCartByUserId(userId).orElseThrow();
    List<GetCartProductResponse> foundCartProducts = cartProductRepository.findCartProductByCartId(
        foundCart.getId()).stream().map(GetCartProductResponse::toDto).toList();

    return new GetCartResponse(userId, foundCart.getId(), foundCartProducts);
  }
}
