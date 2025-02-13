package com.war11.domain.coupon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.war11.domain.coupon.entity.CouponTemplate;
import com.war11.domain.coupon.repository.CouponRepository;
import com.war11.domain.coupon.repository.CouponTemplateRepository;
import com.war11.domain.user.entity.User;
import com.war11.domain.user.repository.UserRepository;
import com.war11.global.exception.base.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class CouponIssueTest {
  @Autowired
  private CouponService couponService;

  @Autowired
  private CouponRepository couponRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CouponTemplateRepository couponTemplateRepository;

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  private CouponTemplate couponTemplate;
  private User user;
  private User user1;
  private User user2;

  @BeforeEach
  void init() {
    user = User.builder()
        .loginId("userId")
        .name("userName")
        .password("userPw")
        .build();
    userRepository.save(user);
    user1 = User.builder()
        .loginId("userId1")
        .name("userName1")
        .password("userPw1")
        .build();
    userRepository.save(user1);
    user2 = User.builder()
        .loginId("userId2")
        .name("userName2")
        .password("userPw2")
        .build();
    userRepository.save(user2);
    couponTemplate = CouponTemplate.builder()
        .name("name")
        .value(1000)
        .quantity(100)
        .startDate(LocalDateTime.now())
        .endDate(LocalDateTime.now().plusDays(1))
        .build();
    couponTemplateRepository.save(couponTemplate);
    couponService.initializeCouponCount();
    redisTemplate.delete(redisTemplate.keys("LOCK:*"));
  }

  @AfterEach
  void end() {
    couponRepository.deleteAll();
    couponTemplateRepository.deleteAll();
    userRepository.deleteAll();
    redisTemplate.delete(redisTemplate.keys("coupon:count:*"));
  }

  @Nested
  @DisplayName("AOP를 활용한 쿠폰 발급 동시성 제어")
  class IssueCouponWithAop{
    @Test
    @DisplayName("동일한 사용자가 동시에 같은 요청을 요구함")
    void 동일한_사용자의_동시_요청이_발생() throws InterruptedException {
      // given
      int threadCount = 3;
      ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
      CountDownLatch latch = new CountDownLatch(threadCount);
      Long  couponTemplateId = couponTemplate.getId();
      Long userId = user.getId();
      AtomicInteger successCount = new AtomicInteger();
      AtomicInteger failCount = new AtomicInteger();
      // when
      for (int i = 0; i < threadCount; i++) {
        executorService.submit(() -> {
          try{
            couponService.issueCoupon(couponTemplateId,userId);
            successCount.incrementAndGet();
          } catch (AccessDeniedException e) {
            failCount.incrementAndGet();
          } finally {
            latch.countDown();
          }
        });
      }
      latch.await();
      // then
      assertEquals(1,successCount.get());
      assertEquals(threadCount-1,failCount.get());
    }

    @Test
    @DisplayName("여러명의 사용자가 동시에 쿠폰 발급 요청")
    void 여러_사용자가_동시에_쿠폰_발급() throws InterruptedException {
      // given
      int threadCount = 3;
      List<Long> userIds = new ArrayList<>();
      userIds.add(user.getId());
      userIds.add(user1.getId());
      userIds.add(user2.getId());
      ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
      CountDownLatch latch = new CountDownLatch(threadCount);
      Long  couponTemplateId = couponTemplate.getId();
      AtomicInteger successCount = new AtomicInteger();
      // when
      for (long id: userIds) {
        executorService.submit(()-> {
          try{
            couponService.issueCoupon(couponTemplateId,id);
            successCount.incrementAndGet();
          } finally {
            latch.countDown();
          }
        });
      }
      latch.await();
      // then
      assertEquals(threadCount,successCount.get());
    }
  }

  @Nested
  @DisplayName("Redisson 을 활용한 쿠폰 발급 동시성 제어")
  class IssueCouponWithRedisson{
    @Test
    @DisplayName("동일한 사용자가 동시에 같은 요청을 요구함")
    void 동일한_사용자의_동시_요청이_발생() throws InterruptedException {
      // given
      int threadCount = 3;
      ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
      CountDownLatch latch = new CountDownLatch(threadCount);
      Long couponTemplateId = couponTemplate.getId();
      Long userId = user.getId();
      AtomicInteger successCount = new AtomicInteger();
      AtomicInteger failCount = new AtomicInteger();
      // when
      for (int i = 0; i < threadCount; i++) {
        executorService.submit(() -> {
          try {
            couponService.issueCouponWithLargeScale(couponTemplateId, userId);
            successCount.incrementAndGet();
          } catch (RuntimeException e) {
            failCount.incrementAndGet();
          } finally {
            latch.countDown();
          }
        });
      }
      latch.await();

          // then
      assertEquals(1, successCount.get());
      assertEquals(threadCount - 1, failCount.get());
    }
    @Test
    @DisplayName("여러명의 사용자가 동시에 쿠폰 발급 요청")
    void 여러_사용자가_동시에_쿠폰_발급() throws InterruptedException {
      // given
      int threadCount = 3;
      List<Long> userIds = new ArrayList<>();
      userIds.add(user.getId());
      userIds.add(user1.getId());
      userIds.add(user2.getId());
      ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
      CountDownLatch latch = new CountDownLatch(threadCount);
      Long couponTemplateId = couponTemplate.getId();
      AtomicInteger successCount = new AtomicInteger();

      // when
      for (long id : userIds) {
        executorService.submit(() -> {
          try {
            couponService.issueCouponWithLargeScale(couponTemplateId, id);
            successCount.incrementAndGet();
          } catch (RuntimeException e) {  // RuntimeException으로 변경
            e.printStackTrace();
          } finally {
            latch.countDown();
          }
        });
      }
      latch.await();
      // then
      assertEquals(threadCount, successCount.get());
    }
  }

  @Nested
  @DisplayName("Lettuce 를 활용한 쿠폰 발급 동시성 제어")
  class IssueCouponWithLettuce {
    @Test
    @DisplayName("동일한 사용자가 동시에 같은 요청을 요구함")
    void 동일한_사용자의_동시_요청이_발생() throws InterruptedException {
      // given
      int threadCount = 3;
      ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
      CountDownLatch latch = new CountDownLatch(threadCount);
      Long couponTemplateId = couponTemplate.getId();
      Long userId = user.getId();
      AtomicInteger successCount = new AtomicInteger();
      AtomicInteger failCount = new AtomicInteger();

      // when
      for (int i = 0; i < threadCount; i++) {
        executorService.submit(() -> {
          try {
            couponService.issueCouponWithLettuce(couponTemplateId, userId);
            successCount.incrementAndGet();
          } catch (RuntimeException e) {
            failCount.incrementAndGet();
          } finally {
            latch.countDown();
          }
        });
      }
      latch.await();

      // then
      assertEquals(1, successCount.get());
      assertEquals(threadCount - 1, failCount.get());
    }
    @Test
    @DisplayName("여러명의 사용자가 동시에 쿠폰 발급 요청")
    void 여러_사용자가_동시에_쿠폰_발급() throws InterruptedException {
      // given
      int threadCount = 3;
      List<Long> userIds = new ArrayList<>();
      userIds.add(user.getId());
      userIds.add(user1.getId());
      userIds.add(user2.getId());
      ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
      CountDownLatch latch = new CountDownLatch(threadCount);
      Long couponTemplateId = couponTemplate.getId();
      AtomicInteger successCount = new AtomicInteger();

      // when
      for (long id : userIds) {
        executorService.submit(() -> {
          try {
            couponService.issueCouponWithLettuce(couponTemplateId, id);
            successCount.incrementAndGet();
          } finally {
            latch.countDown();
          }
        });
      }
      latch.await();

      // then
      assertEquals(threadCount, successCount.get());
    }
  }

}
