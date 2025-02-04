package com.war11.domain.product.dto.request;

import com.war11.domain.product.entity.Product;
import com.war11.domain.product.entity.enums.ProductStatus;
import lombok.Builder;

@Builder
public record ProductUpdateRequest(
    Long id,
    String name,
    String category,
    Long price,
    int quantity,
    ProductStatus status
) {

  public static Product toEntity(ProductUpdateRequest productUpdateRequest){
    return Product.builder()
        .name(productUpdateRequest.name())
        .category(productUpdateRequest.category())
        .price(productUpdateRequest.price())
        .quantity(productUpdateRequest.quantity())
        .status(productUpdateRequest.status())
        .isDeleted(false)
        .build();
  }



}
