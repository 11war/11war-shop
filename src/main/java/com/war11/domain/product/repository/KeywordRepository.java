package com.war11.domain.product.repository;

import com.war11.domain.product.entity.Keyword;
import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface KeywordRepository extends JpaRepository<Keyword,String> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  boolean existsById(String id);

  Page<Keyword> findByKeywordStartingWith(String keyword, Pageable pageable);

  ;
}
