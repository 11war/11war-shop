package com.war11.global.config;

import com.war11.domain.coupon.entity.CouponTemplate;
import com.war11.domain.lock.repository.LockRepository;
import com.war11.domain.lock.service.LockService;
import com.war11.domain.product.entity.Keyword;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class LockConfig {

  @Bean
  public LockRepository<CouponTemplate> couponLockRepository(
      RedisTemplate<String, String> redisTemplate) {
    return new LockRepository<>(redisTemplate, CouponTemplate.class);
  }

  @Bean
  public LockService<CouponTemplate> couponLockService(
      LockRepository<CouponTemplate> lockRepository) {
    return new LockService<>(lockRepository);
  }

  @Bean
  public LockRepository<Keyword> keywordLockRepository(
      RedisTemplate<String, String> redisTemplate) {
    return new LockRepository<>(redisTemplate, Keyword.class);
  }

  @Bean
  public LockService<Keyword> keywordLockService(
      LockRepository<Keyword> lockRepository) {
    return new LockService<>(lockRepository);
  }

}