package com.war11.domain.coupon.service;

import com.war11.domain.coupon.dto.request.CouponTemplateRequest;
import com.war11.domain.coupon.dto.request.CouponTemplateUpdateRequest;
import com.war11.domain.coupon.dto.response.CouponResponse;
import com.war11.domain.coupon.dto.response.CouponTemplateResponse;
import com.war11.domain.coupon.entity.Coupon;
import com.war11.domain.coupon.entity.CouponTemplate;
import com.war11.domain.coupon.repository.CouponRepository;
import com.war11.domain.coupon.repository.CouponTemplateRepository;
import com.war11.domain.order.entity.Order;
import com.war11.domain.order.repository.OrderRepository;
import com.war11.domain.user.entity.User;
import com.war11.domain.user.repository.UserRepository;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {
  private final CouponRepository couponRepository;
  private final CouponTemplateRepository couponTemplateRepository;
  private final OrderRepository orderRepository;
  private final UserRepository userRepository;

  public CouponTemplateResponse generateCouponTemplate(CouponTemplateRequest couponTemplateRequest) {
    CouponTemplate couponTemplate = couponTemplateRequest.toEntity();
    return couponTemplateRepository.save(couponTemplate).toDto();
  }

  public List<CouponTemplateResponse> findCouponTemplates() {
    return couponTemplateRepository.findAll().stream()
        .map(CouponTemplate::toDto)
        .toList();
  }

  public CouponTemplateResponse findCouponTemplate(Long id) {
    return findCouponTemplateById(id).toDto();
  }

  public void editCouponTemplate(Long id, CouponTemplateUpdateRequest updateRequest) {
    CouponTemplate couponTemplate = findCouponTemplateById(id);
    couponTemplate.updateCouponTemplate(updateRequest);
  }

  @Transactional
  public void removeCouponTemplate(Long id) {
    try{
      couponTemplateRepository.deleteById(id);
    } catch (NoSuchElementException e) {
      throw new NoSuchElementException("템플릿이 없음.");
    }
  }

  @Transactional
  public CouponResponse issueCoupon(Long couponTemplateId,Long userId) {
    CouponTemplate couponTemplate = findCouponTemplateById(couponTemplateId);
    User user = userRepository.findById(userId).orElseThrow();
    Coupon coupon = couponTemplate.issueCoupon(user);
    return couponRepository.save(coupon).toDto();
  }

  @Transactional
  public void useCoupon(Long couponId, Long orderId) {
    Coupon coupon = findCouponById(couponId);
    Order order = orderRepository.findById(orderId).orElseThrow();
    coupon.useCoupon(order);
  }

  public List<CouponResponse> findUserCoupons(Long userId) {
    return couponRepository.findAllByUserId(userId).stream()
        .map(Coupon::toDto)
        .toList();
  }

  public CouponResponse findUserCoupon(Long couponId, Long userId) {
    return couponRepository.findByIdAndUserId(couponId,userId).orElseThrow().toDto();
  }

  private CouponTemplate findCouponTemplateById(Long id) {
    return couponTemplateRepository.findById(id).orElseThrow();
  }

  private Coupon findCouponById(Long id) {
    return couponRepository.findById(id).orElseThrow();
  }
}
