package com.war11.domain.product.repository;

import com.war11.domain.product.dto.response.ProductResponse;
import com.war11.domain.product.entity.Product;
import com.war11.global.exception.enums.ErrorCode;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long>,ProductRepositoryCustom {


  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT p FROM Product p WHERE p.id = :id")
  Product findByIdForUpdate(@Param("id") Long id);

}
