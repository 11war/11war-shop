package com.war11.domain.order.entity;

import com.war11.domain.order.dto.response.OrderProductResponse;
import com.war11.domain.order.dto.response.OrderResponse;
import com.war11.domain.order.entity.enums.OrderStatus;
import com.war11.domain.user.entity.User;
import com.war11.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order")
public class Order extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderProduct> orderProducts = new ArrayList<>();

  private Long discountedPrice;

  @Column(nullable = false)
  private Long totalPrice;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  public Order(User user) {
    this.user = user;
  }

  public OrderResponse toDto(List<OrderProductResponse> orderProducts) {
    return OrderResponse.builder()
        .user(user)
        .orderProducts(orderProducts)
        .discountedPrice(discountedPrice)
        .totalPrice(totalPrice)
        .orderStatus(status)
        .createdAt(getCreatedAt())
        .updatedAt(getUpdatedAt())
        .build();
  }
}
