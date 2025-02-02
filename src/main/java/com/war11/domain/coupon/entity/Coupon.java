package com.war11.domain.coupon.entity;

import com.war11.domain.coupon.entity.enums.CouponStatus;
import com.war11.domain.order.entity.Order;
import com.war11.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_coupon_id")
    private CouponTemplate couponTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    private LocalDateTime expireDate;
    private LocalDateTime usedDate;
}
