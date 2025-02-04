package com.war11.domain.product.service;

import com.war11.domain.product.dto.request.ProductRequest;
import com.war11.domain.product.dto.request.ProductUpdateRequest;
import com.war11.domain.product.dto.response.ProductResponse;
import com.war11.domain.product.entity.Product;
import com.war11.domain.product.repository.ProductRepository;
import com.war11.global.exception.BusinessException;
import com.war11.global.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
  private final ProductRepository productRepository;

  public ProductResponse createProduct(ProductRequest productRequest){

    Product resultProduct = productRepository.save(productRequest.toEntity(productRequest));
    return resultProduct.toDto(resultProduct);

  }

  public ProductResponse updateProduct(ProductUpdateRequest productUpdateRequest) {

    Product resultProduct = findById(productUpdateRequest.id());

    resultProduct.updateProduct(productUpdateRequest);
    return resultProduct.toDto(resultProduct);
  }

  public void deleteProduct(Long productId) {

    Product resultProduct = findById(productId);
    resultProduct.deleteProduct();

  }

  public ProductResponse findByProductId(Long productId) {
    Product result = findById(productId);
    return result.toDto(result);
  }

   private Product findById(Long productId){
     return productRepository.findById(productId).
         orElseThrow (() ->
             new BusinessException(ErrorCode.NOT_FOUND_PRODUCT_ID));
   }



   }
