package com.war11.domain.product.repository;

import static com.war11.domain.product.entity.QProduct.product;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.war11.domain.product.dto.request.ProductFindRequest;
import com.war11.domain.product.dto.response.ProductResponse;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

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
             findName(productFindRequest.getName()),
            findCategory(productFindRequest.getCategory()),
            minPrice(productFindRequest.getMinPrice()),
            maxPrice(productFindRequest.getMaxPrice()),
            minQuantity(productFindRequest.getMinQuantity()),
            maxQuantity(productFindRequest.getMaxQuantity()),
        product.updatedAt.between(
            productFindRequest.getStartDateTime(),
            productFindRequest.getEndDateTime()),
            product.status.eq(productFindRequest.getStatus())
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();


long total = Optional.ofNullable(jpaQueryFactory
    .select(product.count())
    .from(product)
    .where(
        findName(productFindRequest.getName()),
        findCategory(productFindRequest.getCategory()),
        minPrice(productFindRequest.getMinPrice()),
        maxPrice(productFindRequest.getMaxPrice()),
        minQuantity(productFindRequest.getMinQuantity()),
        maxQuantity(productFindRequest.getMaxQuantity()),
        product.updatedAt.between(
            productFindRequest.getStartDateTime(),
            productFindRequest.getEndDateTime()),
        product.status.eq(productFindRequest.getStatus())
    )
    .offset(pageable.getOffset())
    .limit(pageable.getPageSize())
    .fetchOne()).orElse(0L);






    return new PageImpl<>(response,pageable,total);
  }

  private BooleanExpression findName(String name) {
    return name != null ? product.name.like("%"+name+"%") : null;
  }
  private BooleanExpression findCategory(String category) {
    return category != null ? product.category.eq(category) : null;
  }

  private BooleanExpression minPrice(Long minPrice){
    return minPrice != -1L ? product.price.goe(minPrice) : null;
  }

  private BooleanExpression maxPrice(Long maxPrice){
    return maxPrice != -1L ? product.price.loe(maxPrice) : null;
  }

  private BooleanExpression minQuantity(int minQuantity){
    return minQuantity != -1 ? product.quantity.goe(minQuantity) : null;
  }

  private BooleanExpression maxQuantity(int maxQuantity){
    return maxQuantity != -1 ? product.quantity.goe(maxQuantity) : null;
  }

}
