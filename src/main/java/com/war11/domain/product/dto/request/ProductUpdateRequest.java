package com.war11.domain.product.dto.request;

public record ProductUpdateRequest(
    Long id,
    String name,
    String category,
    Long price,
    int quantity,
    String status
) {

  public static ProductUpdateRequest toDto(ProductRequest productRequest){
    return new ProductUpdateRequest(
        productRequest.id(),
        productRequest.name(),
        productRequest.category(),
        productRequest.price(),
        productRequest.quantity(),
        productRequest.status()
    );
  }

}
