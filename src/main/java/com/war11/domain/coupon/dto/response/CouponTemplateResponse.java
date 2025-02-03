package com.war11.domain.coupon.dto.response;

import com.war11.domain.coupon.entity.enums.CouponStatus;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CouponTemplateResponse(String name, Integer value, Integer quantity,
                                     CouponStatus status, LocalDateTime startDate,
                                     LocalDateTime endDate) {}
