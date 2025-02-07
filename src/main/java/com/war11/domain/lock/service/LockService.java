package com.war11.domain.lock.service;

import com.war11.domain.lock.repository.LockRepository;
import com.war11.global.exception.base.ConflictException;
import com.war11.global.exception.enums.ErrorCode;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class LockService<T> {
  private final LockRepository<T> lockRepository;
  private static final int WAIT_TIME = 3;

  public void lock(String key) {
    int retryCount = 0;
    while (!lockRepository.lock(key)) {
      if (retryCount >= WAIT_TIME) {
        log.warn(lockRepository.getClass().getSimpleName() + "락 획득 시간 초과");
        throw new ConflictException(ErrorCode.TIME_OUT_LOCK);
      }
      retryCount++;
      try {
        Thread.sleep(100 + new Random().nextInt(100));
      } catch (InterruptedException e) {
        log.warn(lockRepository.getClass().getSimpleName() + "락 획득 중 오류 발생", e);
        throw new ConflictException(ErrorCode.DENIED_GET_LOCK);
      }
    }
  }
  public void unlock(String key) {
    lockRepository.unlock(key);
  }
}
