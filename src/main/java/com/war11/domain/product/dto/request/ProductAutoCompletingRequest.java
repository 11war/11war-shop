package com.war11.domain.product.dto.request;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductAutoCompletingRequest {
  private String keyword;
  private int size=10;
  private int page=1;


}
