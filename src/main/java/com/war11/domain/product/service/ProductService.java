package com.war11.domain.product.service;

import com.war11.domain.product.dto.request.ProductFindRequest;
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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
  private final ProductRepository productRepository;

  public ProductResponse createProduct(ProductRequest productRequest){

    Product resultProduct = productRepository.save(productRequest.toEntity(productRequest));
    return resultProduct.toDto(resultProduct);

  }

  @Transactional
  public ProductResponse updateProduct(ProductUpdateRequest productUpdateRequest) {

    productValidationById(productUpdateRequest.id());
    Product resultProduct = productRepository.findByIdForUpdate(productUpdateRequest.id());
    resultProduct.updateProduct(productUpdateRequest);
    return resultProduct.toDto(resultProduct);
  }

  @Transactional
  public void deleteProduct(Long productId) {

    productValidationById(productId);
    Product resultProduct = productRepository.findByIdForUpdate(productId);
    resultProduct.deleteProduct();
  }

  public ProductResponse findByProductId(Long productId) {
    Product result = productValidationById(productId);
    return result.toDto(result);
  }

   private Product productValidationById(Long productId){
     return productRepository.findById(productId).
         orElseThrow (() ->
             new BusinessException(ErrorCode.NOT_FOUND_PRODUCT_ID));
   }

   public Page<ProductResponse> findByProductName(ProductFindRequest productFindRequest) {

      Order order;
      if(productFindRequest.getOrder().equals("asc")){
        order = Sort.Order.asc("updatedAt");
      }else {
        order = Sort.Order.desc("updatedAt");
      }

     Pageable pageable = PageRequest.of(
         productFindRequest.getPage() - 1,
         productFindRequest.getSize(),
         Sort.by(order));

      return productRepository.findByProductName(productFindRequest,pageable);
   }

   }
