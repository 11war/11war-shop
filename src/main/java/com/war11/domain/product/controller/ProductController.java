package com.war11.domain.product.controller;

import com.war11.domain.product.dto.request.ProductAutoCompletingRequest;
import com.war11.domain.product.dto.request.ProductFindRequest;
import com.war11.domain.product.dto.request.ProductRequest;
import com.war11.domain.product.dto.request.ProductUpdateRequest;
import com.war11.domain.product.dto.response.KeywordResponse;
import com.war11.domain.product.dto.response.ProductResponse;
import com.war11.domain.product.service.ProductService;
import com.war11.global.common.ApiResponse;
import com.war11.global.config.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
  public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
      @Valid @RequestBody ProductRequest productRequest,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    ProductResponse productResponse = productService.createProduct(productRequest,
        userDetails.getUsername());

    return ApiResponse.created(productResponse);
  }

  @PutMapping
  public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
      @Valid @RequestBody ProductUpdateRequest productUpdateRequest,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    ProductResponse productResponse = productService.updateProduct(productUpdateRequest,
        userDetails.getUsername());

    return ApiResponse.success(productResponse);
  }

  @DeleteMapping("/{productId}")
  public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Long productId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    productService.deleteProduct(productId, userDetails.getUsername());
    return ApiResponse.success("삭제되었습니다.");
  }

  @GetMapping("/{productId}")
  public ResponseEntity<ApiResponse<ProductResponse>> findByProductId(
      @PathVariable Long productId) {
    ProductResponse productResponse = productService.findByProductId(productId);
    return ApiResponse.success(productResponse);
  }

  @GetMapping
  public ResponseEntity<ApiResponse<Page<ProductResponse>>> findByProductName(
      @Valid @ModelAttribute ProductFindRequest productFindRequest,
      @PageableDefault(sort = "updatedAt",
          direction = org.springframework.data.domain.Sort.Direction.DESC)
          Pageable pageable) {
    return ApiResponse.success(productService.findByProductName(productFindRequest,pageable));
  }

  @GetMapping("/auto-completing")
  public ResponseEntity<ApiResponse<Page<KeywordResponse>>> findByAutoCompleting(
      @RequestParam(required = false)
      ProductAutoCompletingRequest productAutoCompletingRequest,
      @PageableDefault(sort = "count",
          direction = org.springframework.data.domain.Sort.Direction.DESC)
      Pageable pageable) {
    return ApiResponse.success(productService.findByAutoCompleting(productAutoCompletingRequest,pageable));
  }

}
