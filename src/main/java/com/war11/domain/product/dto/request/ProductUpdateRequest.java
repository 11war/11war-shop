package com.war11.domain.product.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.war11.domain.product.entity.Product;
import com.war11.domain.product.entity.enums.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record ProductUpdateRequest(
    @Min(value = 1, message = "상품고유번호는 빈값일 수 없습니다.")
    @JsonProperty("id")
    Long id,
    @NotBlank(message = "상품명은 빈값일 수 없습니다.")
    @JsonProperty("name")
    String name,
    @NotBlank(message = "카테고리명은 빈값일 수 없습니다.")
    @JsonProperty("category")
    String category,
    @Min(value = 0, message = "가격은 비어있거나 0보다 작을 수 없습니다.")
    @JsonProperty("price")
    Long price,
    @Min(value = 0, message = "수량은 비어있거나 0보다 작을 수 없습니다.")
    @JsonProperty("quantity")
    int quantity,
    @NotNull(message = "상품상태는 빈값일 수 없습니다.")
    @JsonProperty("status")
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
