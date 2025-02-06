package com.war11.domain.order.entity;

import com.war11.domain.order.dto.response.OrderProductResponse;
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
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false, length = 20)
    private String productName;

    @Column(nullable = false)
    private Long productPrice;

    @Column(nullable = false)
    private Integer quantity;

    public OrderProduct(Order order, Long productId, String productName, Long productPrice, Integer quantity) {
        this.productId = productId;
        this.order = order;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

    public OrderProductResponse toDto() {
        return OrderProductResponse.builder()
            .orderId(order.getId())
            .orderProductId(id)
            .productName(productName)
            .productPrice(productPrice)
            .quantity(quantity)
            .build();
    }
}
