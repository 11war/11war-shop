package com.war11.domain.product.repository;

import com.war11.domain.product.entity.Keyword;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword,Long> {

  boolean existsByKeyword(String keyword);

  Page<Keyword> findByKeywordStartingWith(String keyword, Pageable pageable);

  ;
}
