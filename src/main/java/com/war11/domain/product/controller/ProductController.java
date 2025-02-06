package com.war11.domain.product.controller;

import com.war11.domain.product.dto.request.ProductRequest;
import com.war11.domain.product.dto.request.ProductUpdateRequest;
import com.war11.domain.product.dto.response.ProductResponse;
import com.war11.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
  private final ProductService productService;

  @PostMapping
  public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest){
    ProductResponse productResponse = productService.createProduct(productRequest);

    return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
  }

  @PutMapping
  public ResponseEntity<ProductResponse> updateProduct(@RequestBody ProductUpdateRequest productUpdateRequest){
    ProductResponse productResponse = productService.updateProduct(productUpdateRequest);

    return new ResponseEntity<>(productResponse, HttpStatus.OK);
  }

  @DeleteMapping("/{productId}")
  public ResponseEntity<String> deleteProduct(@PathVariable Long productId){
    productService.deleteProduct(productId);
    return new ResponseEntity<>("삭제되었습니다.",HttpStatus.OK);
  }

  @GetMapping("/{productId}")
  public ResponseEntity<ProductResponse> findByProductId(@PathVariable Long productId){
    ProductResponse productResponse = productService.findByProductId(productId);
    return new ResponseEntity<>(productResponse,HttpStatus.OK);
  }
}
