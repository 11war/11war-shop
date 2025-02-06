package com.war11.domain.product.service;

import com.war11.domain.product.dto.request.ProductAutoCompletingRequest;
import com.war11.domain.product.dto.request.ProductFindRequest;
import com.war11.domain.product.dto.request.ProductRequest;
import com.war11.domain.product.dto.request.ProductUpdateRequest;
import com.war11.domain.product.dto.response.KeywordResponse;
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
import org.springframework.data.domain.Pageable;
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

  public ProductResponse createProduct(ProductRequest productRequest, String username) {
    checkAdmin(username);

    Product resultProduct = productRepository.save(productRequest.toEntity(productRequest));
    return resultProduct.toDto(resultProduct);

  }

  @Transactional
  public ProductResponse updateProduct(ProductUpdateRequest productUpdateRequest, String username) {
    checkAdmin(username);

    findByIdOrThrow(productUpdateRequest.id());
    Product resultProduct = productRepository.findByIdForUpdate(productUpdateRequest.id());
    resultProduct.updateProduct(productUpdateRequest);
    return resultProduct.toDto(resultProduct);
  }

  @Transactional
  public void deleteProduct(Long productId, String username) {
    checkAdmin(username);

    findByIdOrThrow(productId);
    Product resultProduct = productRepository.findByIdForUpdate(productId);
    resultProduct.deleteProduct();
  }

  public ProductResponse findByProductId(Long productId) {
    Product result = findByIdOrThrow(productId);
    return result.toDto(result);
  }

  private Product findByIdOrThrow(Long productId) {
    return productRepository.findById(productId).
        orElseThrow(() ->
            new BusinessException(ErrorCode.NOT_FOUND_PRODUCT_ID));
  }

  @Transactional
  public Page<ProductResponse> findByProductName(ProductFindRequest productFindRequest,Pageable pageable) {


    ZSetOperations<String,String> zSetOperations = redisTemplate.opsForZSet();
    zSetOperations.incrementScore("keyword",productFindRequest.name(),1);

    return productRepository.findByProductName(productFindRequest, pageable);
  }

  public Page<KeywordResponse> findByAutoCompleting(ProductAutoCompletingRequest productAutoCompletingRequest,Pageable pageable) {
    LocalDateTime end = LocalDateTime.now();
    LocalDateTime start = end.minusHours(1);

     return keywordRepository.findByKeyword(
        productAutoCompletingRequest.keyword(), start, end, pageable);
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

  //Create,Update,Delete 모두 권한 검사가 핅요하므로, 중복코드 방지를 위해 통합.
  public void checkAdmin(String  username){
    if(!username.equals("ADMIN")){
      throw new BusinessException(ErrorCode.UMATHORIZED_ADMIN);
    }
  }
}
