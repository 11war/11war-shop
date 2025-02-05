package com.war11.domain.lock.repository;

import java.time.Duration;
import org.springframework.data.redis.core.RedisTemplate;

public class LockRepository<T> {
  private final RedisTemplate<String, String> redisTemplate;
  private final Class<T> domain;
  private static final String LOCK_PREFIX = "LOCK:";

  public LockRepository(RedisTemplate<String, String> redisTemplate, Class<T> domainClass) {
    this.redisTemplate = redisTemplate;
    this.domain = domainClass;
  }

  public Boolean lock(String key) {
    return redisTemplate
        .opsForValue()
        .setIfAbsent(LOCK_PREFIX + getDomainPrefix() + key, domain.getSimpleName(), Duration.ofSeconds(3));
  }

  public Boolean unlock(String key) {
    return redisTemplate.delete(LOCK_PREFIX + getDomainPrefix() + key);
  }

  private String getDomainPrefix() {
    return domain.getSimpleName().toUpperCase() + ":";
  }
}
