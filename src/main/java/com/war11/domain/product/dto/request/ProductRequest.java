package com.war11.domain.product.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.war11.domain.product.entity.Product;
import com.war11.domain.product.entity.enums.ProductStatus;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties( ignoreUnknown = true )
public record ProductRequest(
    @NotBlank(message = "상품명은 빈값일 수 없습니다.")
    @JsonProperty("name")
    String name,
    @NotBlank(message = "카테고리명은 빈값일 수 없습니다.")
    @JsonProperty("category")
    String category,
    @NotBlank(message = "가격은 빈값일 수 없습니다.")
    @JsonProperty("price")
    Long price,
    @NotBlank(message = "수량은 빈값일 수 없습니다.")
    @JsonProperty("quantity")
    int quantity,
    @JsonProperty("status")
    ProductStatus status) {

    public Product toEntity(ProductRequest productRequest){
        return Product.builder()
            .name(productRequest.name())
            .category(productRequest.category())
            .price(productRequest.price())
            .quantity(productRequest.quantity())
            .status(ProductStatus.valueOf("AVAILABLE"))
            .isDeleted(false)
            .build();
    }
}
