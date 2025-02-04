package com.war11.domain.coupon.controller;

import com.war11.domain.coupon.dto.request.CouponTemplateRequest;
import com.war11.domain.coupon.dto.request.CouponTemplateUpdateRequest;
import com.war11.domain.coupon.dto.response.CouponResponse;
import com.war11.domain.coupon.dto.response.CouponTemplateResponse;
import com.war11.domain.coupon.service.CouponService;
import com.war11.global.common.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/coupon-template")
@RequiredArgsConstructor
public class AdminCouponController {
  private final CouponService couponService;

  @PostMapping
  public ResponseEntity<ApiResponse<CouponTemplateResponse>> createCouponTemplate(@RequestBody CouponTemplateRequest request) {
    CouponTemplateResponse couponTemplate = couponService.generateCouponTemplate(request);
    return ApiResponse.created(couponTemplate);
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<CouponTemplateResponse>>> getCouponTemplates() {
    List<CouponTemplateResponse> couponTemplates = couponService.findCouponTemplates();
    return ApiResponse.success(couponTemplates);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<CouponTemplateResponse>> getCouponTemplateById(@PathVariable Long id) {
    CouponTemplateResponse couponTemplate = couponService.findCouponTemplate(id);
    return ApiResponse.success(couponTemplate);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> updateCouponTemplate(@PathVariable Long id, CouponTemplateUpdateRequest request) {
    couponService.editCouponTemplate(id, request);
    return ApiResponse.noContent();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteCouponTemplate(@PathVariable Long id) {
    couponService.removeCouponTemplate(id);
    return ApiResponse.noContent();
  }

  @PostMapping("/{id}/issue")
  public ResponseEntity<ApiResponse<CouponResponse>> issueCoupon(@PathVariable Long id, @RequestParam Long userId) {
    CouponResponse coupon = couponService.issueCoupon(id, userId);
    return ApiResponse.created(coupon);
  }
}
