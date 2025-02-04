package com.war11.domain.order.entity;

import com.war11.domain.order.entity.enums.OrderStatus;
import com.war11.domain.user.entity.User;
import com.war11.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "order")
public class Order extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Long discountedPrice;

    @Column(nullable = false)
    private Long totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public Order(User user, Long discountedPrice, Long totalPrice, OrderStatus status) {
        this.user = user;
        this.discountedPrice = discountedPrice;
        this.totalPrice = totalPrice;
        this.status = status;
    }
}
