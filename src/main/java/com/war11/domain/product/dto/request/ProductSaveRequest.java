package com.war11.domain.product.dto.request;

import lombok.Builder;

@Builder
public record ProductSaveRequest(
    String name,
    String category,
    Long price,
    int quantity
) {

  public static ProductSaveRequest toDto(ProductRequest productRequest){
    return ProductSaveRequest.builder()
        .name(productRequest.name())
        .category(productRequest.category())
        .price(productRequest.price())
        .quantity(productRequest.quantity())
        .build();

  }
}
