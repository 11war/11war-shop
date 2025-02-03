package com.war11.domain.product.dto.request;

import lombok.Builder;

@Builder
public record ProductUpdateRequest(
    Long id,
    String name,
    String category,
    Long price,
    int quantity,
    String status
) {

  public static ProductUpdateRequest toDto(ProductRequest productRequest){
    return ProductUpdateRequest.builder()
        .id(productRequest.id())
        .name(productRequest.name())
        .category(productRequest.category())
        .price(productRequest.price())
        .quantity(productRequest.quantity())
        .status(productRequest.status())
        .build();
  }

}
