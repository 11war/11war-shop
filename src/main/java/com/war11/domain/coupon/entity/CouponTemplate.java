package com.war11.domain.coupon.entity;

import com.war11.domain.coupon.entity.enums.CouponStatus;
import com.war11.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class CouponTemplate extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30)
    private String name;

    private Integer value;
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @OneToMany(mappedBy = "couponTemplate")
    private List<Coupon> coupons = new ArrayList<>();
}
