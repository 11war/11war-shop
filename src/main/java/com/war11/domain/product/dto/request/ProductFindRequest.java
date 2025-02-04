package com.war11.domain.product.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.war11.domain.product.entity.enums.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
public class ProductFindRequest{
    @NotBlank(message = "상품명은 빈값일 수 없습니다.")
    String name;
    String category;
    Long minPrice = -1L;
    Long maxPrice = -1L;
    int minQuantity = -1;
    int maxQuantity = -1;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime startDateTime = LocalDateTime.of(0001,01,01,00,00,00);
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime endDateTime = LocalDateTime.of(9999,12,31,23,59,59);
    ProductStatus status = ProductStatus.AVAILABLE;
    String order = "desc";
    int size = 10;
    int page = 1;
}
