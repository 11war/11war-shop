package com.war11.domain.cart.repository;

import com.war11.domain.cart.entity.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.core.RedisHash;

public interface CartRepository extends JpaRepository<Cart, Long>{

  Optional<Cart> findCartByUserId(long userId);
}
