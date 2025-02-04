package com.war11.domain.coupon.repository;

import com.war11.domain.coupon.entity.Coupon;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon,Long> {
  List<Coupon> findAllByUserId(Long userId);
  Optional<Coupon> findByIdAndUserId(Long id, Long userId);
}
