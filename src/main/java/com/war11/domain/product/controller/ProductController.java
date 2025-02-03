package com.war11.domain.product.controller;

import com.war11.domain.product.dto.request.ProductRequest;
import com.war11.domain.product.dto.request.ProductSaveRequest;
import com.war11.domain.product.dto.response.ProductResponse;
import com.war11.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
  private final ProductService productService;

  @PostMapping
  public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest){

    ProductResponse productResponse = productService.createProduct(
        ProductSaveRequest.toDto(productRequest));

    return new ResponseEntity<>(productResponse, HttpStatus.CREATED);

  }
}
