package com.war11.domain.product.dto.request;


public record ProductSaveRequest(
    String name,
    String category,
    Long price,
    int quantity
) {

  public static ProductSaveRequest toDto(ProductRequest productRequest){
    return new ProductSaveRequest(
        productRequest.name(),
        productRequest.category(),
        productRequest.price(),
        productRequest.quantity()
    );
  }
}
