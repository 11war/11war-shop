package com.war11.domain.coupon.entity;

import com.war11.domain.coupon.dto.response.CouponResponse;
import com.war11.domain.coupon.entity.enums.CouponStatus;
import com.war11.domain.order.entity.Order;
import com.war11.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    public void useCoupon(Order order) {
        this.status = CouponStatus.USED;
        this.usedDate = LocalDateTime.now();
        this.order = order;
    }

    public CouponResponse toDto() {
        return CouponResponse.builder()
            .id(this.id)
            .couponName(couponTemplate.getName())
            .value(couponTemplate.getValue())
            .status(this.status)
            .expireDate(this.expireDate)
            .usedDate(this.usedDate)
            .build();
    }
}
