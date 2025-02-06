package com.war11.domain.coupon.repository;

import com.war11.domain.coupon.entity.CouponTemplate;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponTemplateRepository extends JpaRepository<CouponTemplate, Long> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT ct FROM CouponTemplate ct WHERE ct.id = :id")
  Optional<CouponTemplate> findByIdWithLock(Long id);
}
