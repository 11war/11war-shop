package com.war11.domain.product.repository;

import static com.war11.domain.product.entity.QProduct.product;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.war11.domain.product.dto.request.ProductFindRequest;
import com.war11.domain.product.dto.response.ProductResponse;
import com.war11.domain.product.entity.enums.ProductStatus;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  BooleanBuilder builder =  new BooleanBuilder();

  @Override
  public Page<ProductResponse> findByProductName(ProductFindRequest productFindRequest,
      Pageable pageable) {
    List<ProductResponse> response = jpaQueryFactory
        .select(Projections.constructor(
            ProductResponse.class,
            product.id,
            product.name,
            product.category,
            product.price,
            product.quantity,
            product.status,
            product.createdAt,
            product.updatedAt
        ))
        .from(product)
        .where(
             builder,
        product.updatedAt.between(
            productFindRequest.startDateTime(),
            productFindRequest.endDateTime()),
            product.status.eq(ProductStatus.valueOf(productFindRequest.status()))
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();


long total = Optional.ofNullable(jpaQueryFactory
    .select(product.count())
    .from(product)
    .where(
        builder,
        product.updatedAt.between(
            productFindRequest.startDateTime(),
            productFindRequest.endDateTime()),
        product.status.eq(ProductStatus.valueOf(productFindRequest.status()))
    )
    .offset(pageable.getOffset())
    .limit(pageable.getPageSize())
    .fetchOne()).orElse(0L);


    if(productFindRequest.name()!=null){
      builder.and(product.name.like(productFindRequest.name()+"%"));
    }

    if(productFindRequest.category()!=null){
      builder.and(product.category.like(productFindRequest.category()+"%"));
    }

    if(productFindRequest.minPrice()!=null){
      builder.and(product.price.goe(productFindRequest.minPrice()));
    }

    if(productFindRequest.maxPrice()!=null){
      builder.and(product.price.loe(productFindRequest.maxPrice()));
    }

    if(productFindRequest.minQuantity()!=null){
      builder.and(product.quantity.goe(productFindRequest.minQuantity()));
    }

    if(productFindRequest.maxQuantity()!=null){
      builder.and(product.quantity.loe(productFindRequest.maxQuantity()));
    }

    return new PageImpl<>(response,pageable,total);
  }
}
