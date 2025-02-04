package com.war11.domain.cart.repository;

import com.war11.domain.cart.entity.CartProduct;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

  List<CartProduct> findCartProductByCartId(Long cartId);
}
