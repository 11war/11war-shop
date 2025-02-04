package com.war11.domain.coupon.entity;

import com.war11.domain.coupon.dto.request.CouponTemplateRequest;
import com.war11.domain.coupon.dto.request.CouponTemplateUpdateRequest;
import com.war11.domain.coupon.dto.response.CouponTemplateResponse;
import com.war11.domain.coupon.entity.enums.CouponStatus;
import com.war11.domain.user.entity.User;
import com.war11.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    public void updateCouponTemplate(CouponTemplateUpdateRequest updateRequest) {
        this.name = updateRequest.name();
        this.value = updateRequest.value();
        this.quantity = updateRequest.quantity();
        this.status = updateRequest.status();
        this.startDate = updateRequest.startDate();
        this.endDate = updateRequest.endDate();
    }

    public Coupon issueCoupon(User user) {
        if(quantity <= 0) {
            throw new IllegalStateException("쿠폰이 모두 소진되었습니다.");
        }
        this.quantity -= 1;
        return Coupon.builder()
            .couponTemplate(this)
            .user(user)
            .status(CouponStatus.AVAILABLE)
            .expireDate(this.endDate)
            .build();
    }

    public CouponTemplateResponse toDto() {
        return CouponTemplateResponse.builder()
            .name(this.name)
            .value(this.value)
            .quantity(this.quantity)
            .status(this.status)
            .startDate(this.startDate)
            .endDate(this.endDate)
            .build();
    }
}
