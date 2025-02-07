package com.war11.domain.product.repository;

import com.war11.domain.product.dto.request.ProductFindRequest;
import com.war11.domain.product.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {

  Page<ProductResponse> findByProductName(ProductFindRequest productSearchRequest, Pageable pageable);
}
