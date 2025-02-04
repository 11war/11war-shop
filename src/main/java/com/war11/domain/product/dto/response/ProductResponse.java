package com.war11.domain.product.dto.response;

import com.war11.domain.product.entity.Product;
import com.war11.domain.product.entity.enums.ProductStatus;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
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


}
