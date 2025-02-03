package com.war11.domain.product.dto.response;

import com.war11.domain.product.entity.Product;
import com.war11.domain.product.entity.enums.ProductStatus;
import java.time.LocalDateTime;

public record ProductResponse(
    Long id,
    String name,
    String category,
    Long price,
    int quantity,
    ProductStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

  public static ProductResponse toDto(Product product){
    return new ProductResponse(
        product.getId(),
        product.getName(),
        product.getCategory(),
        product.getPrice(),
        product.getQuantity(),
        product.getStatus(),
        product.getCreatedAt(),
        product.getUpdatedAt()


    );
  }
}
