package com.war11.domain.product.service;

import com.war11.domain.lock.service.LockService;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

  private static final String KEYWORD_PREFIX = "keyword";
  private final ProductRepository productRepository;
  private final KeywordRepository keywordRepository;
  private final RedisTemplate<String,String> redisTemplate;
  private final LockService<Keyword> lockService;

  public ProductResponse createProduct(ProductRequest productRequest, String loginId) {
    checkAdmin(loginId);

    Product resultProduct = productRepository.save(productRequest.toEntity(productRequest));
    return resultProduct.toDto(resultProduct);

  }

  @Transactional
  public ProductResponse updateProduct(ProductUpdateRequest productUpdateRequest, String loginId) {
    checkAdmin(loginId);

    findByIdOrThrow(productUpdateRequest.id());
    Product resultProduct = productRepository.findByIdForUpdate(productUpdateRequest.id());
    resultProduct.updateProduct(productUpdateRequest);
    return resultProduct.toDto(resultProduct);
  }

  @Transactional
  public void deleteProduct(Long productId, String loginId) {
    checkAdmin(loginId);

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
    String lockKey = productFindRequest.name();

    lockService.lock(lockKey);

    ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();
    if(!valueOperations.setIfAbsent(KEYWORD_PREFIX+productFindRequest.name(),"1", Duration.ofMillis(3000))){
      Long count = Long.valueOf(valueOperations.get(KEYWORD_PREFIX + productFindRequest.name()));
      valueOperations.set(KEYWORD_PREFIX+productFindRequest.name(),Long.toString(count+1), Duration.ofMillis(3000));
    };

    lockService.unlock(lockKey);
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
    List<Keyword> keywordList = new ArrayList<>();
    LocalDateTime cacheSaveTime = LocalDateTime.now();
    ScanOptions scanOptions = ScanOptions.scanOptions().match(KEYWORD_PREFIX+"*").count(10).build();
    Cursor<String> scan = redisTemplate.scan(scanOptions);
    ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();
    while(scan.hasNext()) {
      String keyword = scan.next();
      String count = valueOperations.get(keyword);
      redisTemplate.delete(keyword);
      keywordList.add(new Keyword(keyword.substring(7),Long.parseLong(count), cacheSaveTime));
    }
    keywordRepository.saveAll(keywordList);

  }

  //Create,Update,Delete 모두 권한 검사가 핅요하므로, 중복코드 방지를 위해 통합.
  public void checkAdmin(String  loginId){
    if(!loginId.equals("ADMIN")){
      throw new BusinessException(ErrorCode.UMATHORIZED_ADMIN);
    }
  }
}
