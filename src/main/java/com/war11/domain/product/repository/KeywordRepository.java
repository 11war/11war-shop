package com.war11.domain.product.repository;

import com.war11.domain.product.entity.Keyword;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword,String> {

  boolean existsById(String id);

  Page<Keyword> findByKeywordContaining(String keyword, Pageable pageable);
  ;
}
