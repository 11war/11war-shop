package com.war11.domain.product.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties( ignoreUnknown = true )
public record ProductRequest(
    @JsonProperty("id")
    Long id,
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
    String status) {
}
