package com.war11.domain.product.controller;

import com.war11.domain.product.dto.request.ProductFindRequest;
import com.war11.domain.product.dto.request.ProductRequest;
import com.war11.domain.product.dto.request.ProductUpdateRequest;
import com.war11.domain.product.dto.response.ProductResponse;
import com.war11.domain.product.service.ProductService;
import com.war11.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
  private final ProductService productService;

  @PostMapping
  public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody ProductRequest productRequest){

    ProductResponse productResponse = productService.createProduct(productRequest);

    return ApiResponse.created(productResponse);
  }

  @PutMapping
  public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@RequestBody ProductUpdateRequest productUpdateRequest){
    ProductResponse productResponse = productService.updateProduct(productUpdateRequest);

    return ApiResponse.success(productResponse);
  }

  @DeleteMapping("/{productId}")
  public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Long productId){
    productService.deleteProduct(productId);
    return ApiResponse.success("삭제되었습니다.");
  }

  @GetMapping("/{productId}")
  public ResponseEntity<ApiResponse<ProductResponse>> findByProductId(@PathVariable Long productId){
    ProductResponse productResponse = productService.findByProductId(productId);
    return ApiResponse.success(productResponse);
  }

  @GetMapping
  public Page<ProductResponse> findByProductName(@RequestParam(required=false) ProductFindRequest productFindRequest) {
    return productService.findByProductName(productFindRequest);
  }
}
