package com.war11.domain.product.service;

import com.war11.domain.product.dto.request.ProductSaveRequest;
import com.war11.domain.product.dto.response.ProductResponse;
import com.war11.domain.product.entity.Product;
import com.war11.domain.product.repository.ProductRepository;
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
}
