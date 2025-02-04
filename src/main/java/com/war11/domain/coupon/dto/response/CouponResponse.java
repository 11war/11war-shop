package com.war11.domain.coupon.dto.response;

import com.war11.domain.coupon.entity.enums.CouponStatus;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CouponResponse(Long id, int value, CouponStatus status, String couponName,
                             LocalDateTime expireDate, LocalDateTime usedDate) {}
