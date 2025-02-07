package com.war11.global.util;

import com.war11.global.exception.base.ConflictException;
import com.war11.global.exception.enums.ErrorCode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class LockAspect {
  private static final int MAX_LOCK_SIZE = 1000;
  private static final Map<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>(MAX_LOCK_SIZE);

  @Around("@annotation(com.war11.domain.coupon.annotation.Lock)")
  public Object lockPointcut(ProceedingJoinPoint joinPoint) throws Throwable {
    Long key = (Long) joinPoint.getArgs()[0];
    ReentrantLock reentrantLock = lockMap.computeIfAbsent(key, k -> new ReentrantLock());
    try {
      if (!reentrantLock.tryLock(3, TimeUnit.SECONDS)) {
        throw new ConflictException(ErrorCode.TIME_OUT_LOCK);
      }
      try {
        return joinPoint.proceed();
      } finally {
        reentrantLock.unlock();
      }
    } catch (InterruptedException e) {
      throw new ConflictException((ErrorCode.DENIED_GET_LOCK) );
    }
  }
}
