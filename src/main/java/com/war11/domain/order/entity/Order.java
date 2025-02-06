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
@Table(name = "`order`")
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

  public void updateOrderDetails(Long discountedPrice, List<OrderProduct> orderProducts) {
    this.discountedPrice = discountedPrice;
    this.totalPrice = orderProducts.stream()
        .mapToLong(orderProduct -> orderProduct.getProductPrice() * orderProduct.getQuantity())
        .sum();
    this.status = OrderStatus.PAID;
  }

  public OrderResponse toDto(List<OrderProductResponse> orderProducts) {
    return OrderResponse.builder().orderId(id).orderProducts(orderProducts)
        .totalPrice(totalPrice).discountedPrice(discountedPrice)
        .resultPrice(totalPrice - discountedPrice).orderStatus(status)
        .createdAt(getCreatedAt()).updatedAt(getUpdatedAt()).build();
  }

  public void updateOrderStatus(OrderStatus newStatus) {
    this.status = newStatus;
  }

  public void cancelThisOrder() {
    this.status = OrderStatus.CANCELLED;
  }
}
