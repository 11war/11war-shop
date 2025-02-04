package com.war11.domain.order.entity;

import com.war11.domain.order.dto.response.OrderProductResponse;
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
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Long productPrice;

    @Column(nullable = false)
    private Integer quantity;

    public OrderProductResponse toDto() {
        return OrderProductResponse.builder()
            .order(order)
            .productName(productName)
            .productPrice(productPrice)
            .quantity(quantity)
            .build();
    }
}
