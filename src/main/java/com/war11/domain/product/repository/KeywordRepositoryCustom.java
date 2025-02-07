package com.war11.domain.product.repository;

import com.war11.domain.product.dto.response.KeywordResponse;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface KeywordRepositoryCustom {

  Page<KeywordResponse> findByKeyword(String keyword,LocalDateTime start, LocalDateTime end, Pageable pageable);
}
