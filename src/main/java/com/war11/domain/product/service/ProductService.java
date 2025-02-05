package com.war11.domain.product.service;

import com.war11.domain.product.dto.request.ProductAutoCompletingRequest;
import com.war11.domain.product.dto.request.ProductFindRequest;
import com.war11.domain.product.dto.request.ProductRequest;
import com.war11.domain.product.dto.request.ProductUpdateRequest;
import com.war11.domain.product.dto.response.ProductResponse;
import com.war11.domain.product.entity.Keyword;
import com.war11.domain.product.entity.Product;
import com.war11.domain.product.repository.KeywordRepository;
import com.war11.domain.product.repository.ProductRepository;
import com.war11.global.exception.BusinessException;
import com.war11.global.exception.enums.ErrorCode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final KeywordRepository keywordRepository;
  private final RedisTemplate<String,String> redisTemplate;

  public ProductResponse createProduct(ProductRequest productRequest) {

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

  private Product productValidationById(Long productId) {
    return productRepository.findById(productId).
        orElseThrow(() ->
            new BusinessException(ErrorCode.NOT_FOUND_PRODUCT_ID));
  }

  @Transactional
  public Page<ProductResponse> findByProductName(ProductFindRequest productFindRequest) {

    Order order;
    if (productFindRequest.getOrder().equals("asc")) {
      order = Sort.Order.asc("updatedAt");
    } else {
      order = Sort.Order.desc("updatedAt");
    }

    Pageable pageable = PageRequest.of(
        productFindRequest.getPage() - 1,
        productFindRequest.getSize(),
        Sort.by(order));

    ZSetOperations<String,String> zSetOperations = redisTemplate.opsForZSet();
    zSetOperations.incrementScore("keyword",productFindRequest.getName(),1);


//    if (keywordRepository.existsById(productFindRequest.getName())) {
//      Keyword keyword = keywordRepository.findById(productFindRequest.getName()).orElseThrow(()
//          -> new BusinessException(ErrorCode.NOT_FOUND_KEYWORD_ID));
//      keyword.incrementCount();
//    } else {
//      keywordRepository.save(new Keyword(productFindRequest.getName(), 1L));
//    }

    return productRepository.findByProductName(productFindRequest, pageable);
  }

  public Page<Keyword> findByAutoCompleting(ProductAutoCompletingRequest productAutoCompletingRequest) {
    Order order = Sort.Order.desc("count");

    Pageable pageable = PageRequest.of(
        productAutoCompletingRequest.getPage() - 1,
        productAutoCompletingRequest.getSize(),
        Sort.by(order));

    return keywordRepository.findByKeywordStartingWith(productAutoCompletingRequest.getKeyword(),pageable);
  }

  @Scheduled(fixedRate = 300000)
  public void saveAllCacheToDB() {
    LocalDateTime cacheSaveTime = LocalDateTime.now();
    ZSetOperations<String,String> zSetOperations = redisTemplate.opsForZSet();
    Set<ZSetOperations.TypedTuple<String>> keywordZSetOperations = zSetOperations.rangeWithScores("keyword",0,-1);
    List<Keyword> keywordList = new ArrayList<>();
    keywordZSetOperations.stream().forEach(
        keyword -> {
          keywordList.add(new Keyword(keyword.getValue(),keyword.getScore().longValue(), cacheSaveTime));
        }
    );

    keywordRepository.saveAll(keywordList);
    redisTemplate.delete("keyword");
  }
}
