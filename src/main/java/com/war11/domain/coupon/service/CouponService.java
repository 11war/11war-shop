package com.war11.domain.coupon.service;

import com.war11.domain.coupon.annotation.Lock;
import com.war11.domain.coupon.dto.request.CouponTemplateRequest;
import com.war11.domain.coupon.dto.request.CouponTemplateUpdateRequest;
import com.war11.domain.coupon.dto.response.CouponResponse;
import com.war11.domain.coupon.dto.response.CouponTemplateResponse;
import com.war11.domain.coupon.entity.Coupon;
import com.war11.domain.coupon.entity.CouponTemplate;
import com.war11.domain.coupon.repository.CouponRepository;
import com.war11.domain.coupon.repository.CouponTemplateRepository;
import com.war11.domain.lock.service.LockService;
import com.war11.domain.order.entity.Order;
import com.war11.domain.order.repository.OrderRepository;
import com.war11.domain.user.entity.User;
import com.war11.domain.user.repository.UserRepository;
import com.war11.global.exception.base.AccessDeniedException;
import com.war11.global.exception.base.InvalidRequestException;
import com.war11.global.exception.base.NotFoundException;
import com.war11.global.exception.enums.ErrorCode;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {
  private final CouponRepository couponRepository;
  private final CouponTemplateRepository couponTemplateRepository;
  private final OrderRepository orderRepository;
  private final UserRepository userRepository;
  private final LockService<CouponTemplate> lockService;

  private final RedissonClient redissonClient;
  private final RedisTemplate<String, String> redisTemplate;
  private static final String COUNT_KEY_PREFIX = "coupon:count:";
  private static final String USER_KEY_PREFIX = "coupon:users:";
  private static final String LOCK_KEY_PREFIX = "coupon:lock:";
  private static final long WAIT_TIME = 3L;
  private static final long AVAILABLE_TIME = 5L; // 락 임대 시간

  @PostConstruct
  public void initializeCouponCount() {
    List<CouponTemplate> templates = couponTemplateRepository.findAll();
    for (CouponTemplate template : templates) {
      String countKey = COUNT_KEY_PREFIX + template.getId();
      String userKey = USER_KEY_PREFIX + template.getId();

      // 수량 초기화
      redisTemplate.opsForValue()
          .setIfAbsent(countKey, String.valueOf(template.getQuantity()));

      // 만료 시간 설정
      if (template.getEndDate() != null) {
        Instant endInstant = template.getEndDate().atZone(ZoneId.systemDefault()).toInstant();
        redisTemplate.expireAt(countKey, endInstant);
        redisTemplate.expireAt(userKey, endInstant);
      }
    }
  }

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

  @Transactional
  public void editCouponTemplate(Long id, CouponTemplateUpdateRequest updateRequest) {
    CouponTemplate couponTemplate = findCouponTemplateById(id);
    couponTemplate.updateCouponTemplate(updateRequest);
  }

  @Transactional
  public void removeCouponTemplate(Long id) {
    try{
      couponTemplateRepository.deleteById(id);
    } catch (NoSuchElementException e) {
      throw new NotFoundException(ErrorCode.COUPON_TEMPLATE_NOT_FOUND);
    }
  }

  @Lock
  public CouponResponse issueCoupon(Long couponTemplateId,Long userId) {
    CouponTemplate couponTemplate = findCouponTemplateById(couponTemplateId);
    User user = userRepository.findById(userId).orElseThrow(()-> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    validateCouponDuplicate(couponTemplateId, userId);
    Coupon coupon = couponTemplate.issueCoupon(user);
    return couponRepository.save(coupon).toDto();
  }

  public CouponResponse issueCouponWithLargeScale(Long couponTemplateId, Long userId) {
    RLock lock = redissonClient.getLock(LOCK_KEY_PREFIX + couponTemplateId + ":" + userId);
    User user = userRepository.findById(userId).orElseThrow(()-> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    try {
      boolean isLocked = lock.tryLock(WAIT_TIME, AVAILABLE_TIME, TimeUnit.SECONDS);
      if (!isLocked) {
        throw new AccessDeniedException(ErrorCode.TIME_OUT_COUPON_ISSUE);
      }
      validateCouponDuplicate(couponTemplateId, userId);
      CouponTemplate couponTemplate = findCouponTemplateById(couponTemplateId);
      String countKey = COUNT_KEY_PREFIX + couponTemplateId;
      Long remainingCount = redisTemplate.opsForValue().decrement(countKey);
      if (remainingCount < 0) {// 수량이 부족한 경우 롤백
        redisTemplate.opsForValue().increment(countKey);
        throw new InvalidRequestException(ErrorCode.COUPON_SOLD_OUT);
      }
      Coupon coupon = couponTemplate.issueCoupon(user);
      couponTemplateRepository.save(couponTemplate);
      return couponRepository.save(coupon).toDto();

    } catch (InterruptedException e) {
      throw new AccessDeniedException(ErrorCode.COUPON_ISSUE_DENIED);
    }finally {
      if (lock.isLocked() && lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }
  }

  public CouponResponse issueCouponWithLettuce(Long couponTemplateId, Long userId) {
    String lockKey = couponTemplateId + ":" + userId;
    User user = userRepository.findById(userId).orElseThrow(()-> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    String countKey = COUNT_KEY_PREFIX + couponTemplateId;
    try {
      lockService.lock(lockKey);

      CouponTemplate couponTemplate = couponTemplateRepository.findById(couponTemplateId).orElseThrow(()-> new NotFoundException(ErrorCode.COUPON_TEMPLATE_NOT_FOUND));
      validateCouponDuplicate(couponTemplateId, userId);
      Long remainingCount = redisTemplate.opsForValue().decrement(countKey);

      if (remainingCount < 0) {
        redisTemplate.opsForValue().increment(countKey);
        throw new AccessDeniedException(ErrorCode.COUPON_SOLD_OUT);
      }

      Coupon coupon = couponTemplate.issueCoupon(user);
      couponTemplateRepository.save(couponTemplate);
      return couponRepository.save(coupon).toDto();

    } finally {
      lockService.unlock(lockKey);
    }
  }

  private void validateCouponDuplicate(Long couponTemplateId, Long userId) {
    boolean couponInfo = couponRepository.existsByCouponTemplateIdAndUserId(couponTemplateId,
        userId);
    if(couponInfo) {
      throw new AccessDeniedException(ErrorCode.ALREADY_ISSUED);
    }
  }

  @Transactional
  public void useCoupon(Long couponId, Long orderId) {
    Coupon coupon = findCouponById(couponId);
    Order order = orderRepository.findById(orderId).orElseThrow(()-> new NotFoundException(ErrorCode.ORDER_NOT_FOUND));
    coupon.useCoupon(order);
  }

  public List<CouponResponse> findUserCoupons(Long userId) {
    return couponRepository.findAllByUserId(userId).stream()
        .map(Coupon::toDto)
        .toList();
  }

  public CouponResponse findUserCoupon(Long couponId, Long userId) {
    return couponRepository.findByIdAndUserId(couponId,userId).orElseThrow(() -> new NotFoundException(ErrorCode.COUPON_NOT_FOUND)).toDto();
  }

  private CouponTemplate findCouponTemplateById(Long id) {
    return couponTemplateRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorCode.COUPON_TEMPLATE_NOT_FOUND));
  }

  private Coupon findCouponById(Long id) {
    return couponRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorCode.COUPON_NOT_FOUND));
  }
}
