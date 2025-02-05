package com.war11.domain.coupon.service;

import static org.junit.jupiter.api.Assertions.*;

import com.war11.domain.coupon.entity.CouponTemplate;
import com.war11.domain.coupon.repository.CouponRepository;
import com.war11.domain.coupon.repository.CouponTemplateRepository;
import com.war11.domain.user.entity.User;
import com.war11.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
  }

  @Test
  @DisplayName("동일한 사용자가 동시에 같은 요청을 요구함")
  void 동일한_사용자의_동시_요청이_발생() throws InterruptedException {
    // given
    int threadCount = 3;
    ExecutorService executorService = Executors.newFixedThreadPool(16);
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
        } catch (IllegalStateException e) {
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
    ExecutorService executorService = Executors.newFixedThreadPool(16);
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
