package com.war11.domain.order.dto.request;

import com.war11.domain.coupon.entity.Coupon;

public record OrderRequest(
    Coupon coupon
) {

}
