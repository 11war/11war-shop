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
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

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
      throw new NoSuchElementException("템플릿이 없음.");
    }
  }

  @Lock
  public CouponResponse issueCoupon(Long couponTemplateId,Long userId) {
    CouponTemplate couponTemplate = findCouponTemplateById(couponTemplateId);
    User user = userRepository.findById(userId).orElseThrow();
    validateCouponDuplicate(couponTemplateId, userId);
    Coupon coupon = couponTemplate.issueCoupon(user);
    return couponRepository.save(coupon).toDto();
  }

  public CouponResponse issueCouponWithLargeScale(Long couponTemplateId, Long userId) {
    RLock lock = redissonClient.getLock(LOCK_KEY_PREFIX + couponTemplateId + ":" + userId);
    User user = userRepository.findById(userId).orElseThrow();
    try {
      boolean isLocked = lock.tryLock(WAIT_TIME, AVAILABLE_TIME, TimeUnit.SECONDS);
      if (!isLocked) {
        throw new RuntimeException("쿠폰 발급 대기 시간 초과");
      }
      validateCouponDuplicate(couponTemplateId, userId);
      CouponTemplate couponTemplate = findCouponTemplateById(couponTemplateId);
      String countKey = COUNT_KEY_PREFIX + couponTemplateId;
      Long remainingCount = redisTemplate.opsForValue().decrement(countKey);
      if (remainingCount < 0) {// 수량이 부족한 경우 롤백
        redisTemplate.opsForValue().increment(countKey);
        throw new RuntimeException("모든 쿠폰 수량 소진");
      }
      Coupon coupon = couponTemplate.issueCoupon(user);
      couponTemplateRepository.save(couponTemplate);
      return couponRepository.save(coupon).toDto();

    } catch (InterruptedException e) {
      throw new RuntimeException("쿠폰 발급 오류가 발생", e);
    }finally {
      if (lock.isLocked() && lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public CouponResponse issueCouponWithLettuce(Long couponTemplateId, Long userId) {
    String lockKey = couponTemplateId + ":" + userId;
    User user = userRepository.findById(userId).orElseThrow();
    String countKey = COUNT_KEY_PREFIX + couponTemplateId;
    try {
      lockService.lock(lockKey);

      CouponTemplate couponTemplate = couponTemplateRepository.findByIdWithLock(couponTemplateId).orElseThrow();
      validateCouponDuplicate(couponTemplateId, userId);
      Long remainingCount = redisTemplate.opsForValue().decrement(countKey);

      if (remainingCount < 0) {
        redisTemplate.opsForValue().increment(countKey);
        throw new RuntimeException("모든 쿠폰 수량 소진");
      }

      Coupon coupon = couponTemplate.issueCoupon(user);
      return couponRepository.save(coupon).toDto();

    } finally {
      lockService.unlock(lockKey);
    }
  }

  private void validateCouponDuplicate(Long couponTemplateId, Long userId) {
    boolean couponInfo = couponRepository.existsByCouponTemplateIdAndUserId(couponTemplateId,
        userId);
    if(couponInfo) {
      throw new IllegalStateException("이미 발급된 쿠폰입니다.");
    }
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
