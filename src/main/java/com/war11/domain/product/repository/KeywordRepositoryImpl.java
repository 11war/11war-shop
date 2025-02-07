package com.war11.domain.product.repository;

import static com.war11.domain.product.entity.QKeyword.keyword1;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.war11.domain.product.dto.response.KeywordResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class KeywordRepositoryImpl implements KeywordRepositoryCustom {
  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Page<KeywordResponse> findByKeyword(String keyword, LocalDateTime start, LocalDateTime end, Pageable pageable) {
    List<KeywordResponse> response = jpaQueryFactory
        .select(Projections.constructor(
            KeywordResponse.class,
            keyword1.keyword,
            keyword1.count.sum()))
        .from(keyword1)
        .where(
            keyword1.keyword.like(keyword+"%"),
            keyword1.cacheSaveTime.between(start,end))
        .groupBy(keyword1.keyword)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    long total = Optional.ofNullable(jpaQueryFactory
        .select(keyword1.keyword.count())
        .distinct()
        .from(keyword1)
        .where(
            keyword1.keyword.like(keyword+"%"),
            keyword1.cacheSaveTime.between(start,end))
                .fetchOne()).orElse(0L);


    return new PageImpl<>(response,pageable,total);
  }
}
