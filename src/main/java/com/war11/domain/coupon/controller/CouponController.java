package com.war11.domain.coupon.controller;

import com.war11.domain.coupon.dto.response.CouponResponse;
import com.war11.domain.coupon.service.CouponService;
import com.war11.global.common.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getMyCoupons(Long userId) {
        List<CouponResponse> userCoupons = couponService.findUserCoupons(userId);
        return ApiResponse.success(userCoupons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponResponse>> getMyCouponById(@PathVariable Long id, Long userId) {
        CouponResponse userCoupon = couponService.findUserCoupon(id, userId);
        return ApiResponse.success(userCoupon);
    }

    @PostMapping("{id}/use")
    public ResponseEntity<ApiResponse<Void>> useCoupon(@PathVariable Long id, Long orderId) {
        couponService.useCoupon(id,orderId);
        return ApiResponse.noContentAndSendMessage("쿠폰 사용 완료");//추후에 Enum 으로 수정
    }
}
