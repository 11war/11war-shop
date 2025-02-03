package com.war11.domain.coupon.dto.request;

import com.war11.domain.coupon.entity.CouponTemplate;
import com.war11.domain.coupon.entity.enums.CouponStatus;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CouponTemplateRequest(String name, Integer value, Integer quantity, CouponStatus status,
                                    LocalDateTime startDate, LocalDateTime endDate) {
  public CouponTemplate toEntity() {
    return CouponTemplate.builder()
        .name(name)
        .value(value)
        .quantity(quantity)
        .status(status)
        .startDate(startDate)
        .endDate(endDate)
        .build();
  }
}
