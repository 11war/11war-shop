package com.war11.domain.cart.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateCartProductRequest {

  @Min(value = 1, message = "최소 수량은 1개입니다.")
  Integer quantity;

}
