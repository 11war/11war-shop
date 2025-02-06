package com.war11.domain.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public record ProductFindRequest(@NotBlank(message = "상품명은 빈값일 수 없습니다.")
                                 String name,
                                 String category,
                                 Long minPrice,
                                 Long maxPrice,
                                 Integer minQuantity,
                                 Integer maxQuantity,
                                 @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
                                 LocalDateTime startDateTime,
                                 @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
                                 LocalDateTime endDateTime,
                                 String status
                                 ){
}
