package com.war11.domain.coupon.controller;

import com.war11.domain.coupon.dto.request.CouponTemplateRequest;
import com.war11.domain.coupon.dto.request.CouponTemplateUpdateRequest;
import com.war11.domain.coupon.dto.response.CouponResponse;
import com.war11.domain.coupon.dto.response.CouponTemplateResponse;
import com.war11.domain.coupon.service.CouponService;
import com.war11.global.common.ApiResponse;
import com.war11.global.config.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/coupon-template")
@RequiredArgsConstructor
@Tag(name = "관리자 쿠폰 API", description = "관리자 쿠폰 API 목록임!")
public class AdminCouponController {
  private final CouponService couponService;

  @Operation(                    // @ApiOperation 대체
      summary = "쿠폰 템플릿 생성",
      description = "쿠폰을 생성합니다."
  )
  @PostMapping
  public ResponseEntity<ApiResponse<CouponTemplateResponse>> createCouponTemplate(@RequestBody CouponTemplateRequest request) {
    CouponTemplateResponse couponTemplate = couponService.generateCouponTemplate(request);
    return ApiResponse.created(couponTemplate);
  }

  @Operation(                    // @ApiOperation 대체
      summary = "모든 관리자 쿠폰 조회",
      description = "모든 관리자 쿠폰을 조회합니다."
  )
  @GetMapping
  public ResponseEntity<ApiResponse<List<CouponTemplateResponse>>> getCouponTemplates() {
    List<CouponTemplateResponse> couponTemplates = couponService.findCouponTemplates();
    return ApiResponse.success(couponTemplates);
  }

  @Operation(                    // @ApiOperation 대체
      summary = "특정 관리자 쿠폰 조회",
      description = "특정 관리자 쿠폰을 조회합니다."
  )
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<CouponTemplateResponse>> getCouponTemplateById(@PathVariable Long id) {
    CouponTemplateResponse couponTemplate = couponService.findCouponTemplate(id);
    return ApiResponse.success(couponTemplate);
  }

  @Operation(                    // @ApiOperation 대체
      summary = "관리자 쿠폰 수정",
      description = "관리자 쿠폰을 수정합니다."
  )
  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> updateCouponTemplate(@PathVariable Long id, @RequestBody CouponTemplateUpdateRequest request) {
    couponService.editCouponTemplate(id, request);
    return ApiResponse.noContent();
  }

  @Operation(                    // @ApiOperation 대체
      summary = "관리자 쿠폰 삭제",
      description = "관리자 쿠폰을 삭제합니다."
  )
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteCouponTemplate(@PathVariable Long id) {
    couponService.removeCouponTemplate(id);
    return ApiResponse.noContent();
  }

  @Operation(                    // @ApiOperation 대체
      summary = "사용자 쿠폰 발급",
      description = "(AOP)사용자에게 쿠폰을 발급합니다."
  )
  @PostMapping("/{id}/issue")
  public ResponseEntity<ApiResponse<CouponResponse>> issueCoupon(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
    CouponResponse coupon = couponService.issueCoupon(id, userDetails.getId());
    return ApiResponse.created(coupon);
  }

  @Operation(                    // @ApiOperation 대체
      summary = "사용자 쿠폰 발급",
      description = "(redisson)사용자에게 쿠폰을 발급합니다."
  )
  @PostMapping("/{id}/issue-for-large-scale")
  public ResponseEntity<ApiResponse<CouponResponse>> issueCouponForLargeScale(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
    CouponResponse coupon = couponService.issueCouponWithLargeScale(id, userDetails.getId());
    return ApiResponse.created(coupon);
  }

  @Operation(                    // @ApiOperation 대체
      summary = "사용자 쿠폰 발급",
      description = "(Lettuce)사용자에게 쿠폰을 발급합니다."
  )
  @PostMapping("/{id}/issue-for-large-scale-with-lettuce")
  public ResponseEntity<ApiResponse<CouponResponse>> issueCouponForLargeScaleWithLettuce(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
    CouponResponse coupon = couponService.issueCouponWithLettuce(id, userDetails.getId());
    return ApiResponse.created(coupon);
  }
}
