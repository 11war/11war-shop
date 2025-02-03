package com.war11.domain.coupon.dto.request;

import com.war11.domain.coupon.entity.enums.CouponStatus;
import java.time.LocalDateTime;

public record CouponTemplateUpdateRequest(String name, Integer value, Integer quantity,
                                          CouponStatus status, LocalDateTime startDate,
                                          LocalDateTime endDate) {}
