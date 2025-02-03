package com.war11.domain.product.service;

import com.war11.domain.product.dto.request.ProductSaveRequest;
import com.war11.domain.product.dto.request.ProductUpdateRequest;
import com.war11.domain.product.dto.response.ProductResponse;
import com.war11.domain.product.entity.Product;
import com.war11.domain.product.repository.ProductRepository;
import com.war11.global.exception.BusinessException;
import com.war11.global.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
  private final ProductRepository productRepository;

  public ProductResponse createProduct(ProductSaveRequest productSaveRequest){

    Product resultProduct = productRepository.save(Product.toEntity(productSaveRequest));
    return ProductResponse.toDto(resultProduct);

  }

  public ProductResponse updateProduct(ProductUpdateRequest productUpdateRequest) {

    if(productUpdateRequest.id()==null||productUpdateRequest.status()==null){
      throw new BusinessException(ErrorCode.NOT_FOUND_PRODUCT_ID);
    }

    Product resultProduct = productRepository.findById(productUpdateRequest.id()).
        orElseThrow (() ->
            new BusinessException(ErrorCode.NOT_FOUND_PRODUCT_ID));

    resultProduct.updateProduct(productUpdateRequest);
    return ProductResponse.toDto(resultProduct);
  }

  public void deleteProduct(Long productId) {

    Product resultProduct = productRepository.findById(productId).
        orElseThrow (() ->
            new BusinessException(ErrorCode.NOT_FOUND_PRODUCT_ID));
    resultProduct.deleteProduct();

  }
}
