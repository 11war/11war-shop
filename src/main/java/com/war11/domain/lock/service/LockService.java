package com.war11.domain.lock.service;

import com.war11.domain.lock.repository.LockRepository;
import java.util.Random;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LockService<T> {
  private final LockRepository<T> lockRepository;
  private static final int WAIT_TIME = 3;

  public void lock(String key) {
    int retryCount = 0;
    while (!lockRepository.lock(key)) {
      if (retryCount >= WAIT_TIME) {
        throw new RuntimeException(lockRepository.getClass().getSimpleName() + "락 획득 시간 초과");
      }
      retryCount++;
      try {
        Thread.sleep(100 + new Random().nextInt(100));
      } catch (InterruptedException e) {
        throw new RuntimeException(lockRepository.getClass().getSimpleName() + "락 획득 중 오류 발생", e);
      }
    }
  }
  public void unlock(String key) {
    lockRepository.unlock(key);
  }
}
