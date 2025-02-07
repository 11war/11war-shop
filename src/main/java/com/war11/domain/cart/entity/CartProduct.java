package com.war11.domain.cart.entity;

import com.war11.domain.cart.dto.response.CartProductResponse;
import com.war11.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartProduct {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cart_id")
  private Cart cart;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;

  @Column(nullable = false)
  private Integer quantity;

  @Column(nullable = false)
  private boolean isChecked;

  public void updateQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public void toggleCheck() {
    isChecked = !isChecked;
  }

  public CartProductResponse toDto() {
    return CartProductResponse.builder()
        .cartId(cart.getId())
        .productId(product.getId())
        .productName(product.getName())
        .productPrice(product.getPrice())
        .productQuantity(quantity)
        .isChecked(isChecked)
        .build();
  }
}
