package com.war11.domain.product.service;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.war11.domain.product.dto.request.ProductRequest;
import com.war11.domain.product.dto.request.ProductUpdateRequest;
import com.war11.domain.product.dto.response.ProductResponse;
import com.war11.domain.product.entity.Product;
import com.war11.domain.product.repository.ProductRepository;
import java.lang.reflect.Field;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ReflectionUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Save Service test")
public class ProductServiceTest {

  @InjectMocks
  private ProductService productService;

  @Mock
  private ProductRepository productRepository;

  Product product;

  @BeforeEach
  void 상품_생성_전처리(){

    final String name = "갤럭시북";
    final String category = "노트북";
    final Long price = 1270000L;
    final int quantity = 15;
  product = Product.builder()
      .name(name)
      .category(category)
      .price(price)
      .quantity(quantity)
      .build();
  }

  @Test
  void 상품_저장_성공() {
    //given

    ProductRequest productRequest = new ProductRequest(
        product.getName(),
        product.getCategory(),
        product.getPrice(),
        product.getQuantity());


    when(productRepository.save(any(Product.class))).thenReturn(product);

    //when
    ProductResponse actualResult = productService.createProduct(productRequest,
        "ADMIN");

    //then
    ProductResponse expectResult = product.toDto(product);
    assertThat(actualResult)
        .usingRecursiveComparison()
        .isEqualTo(expectResult);
    verify(productRepository, times(1)).save(any(Product.class));
  }

  @Test
   void 상품_수정_성공() {

    //given

    ProductUpdateRequest productUpdateRequest = new ProductUpdateRequest(
        1L,
        product.getName(),
        product.getCategory(),
        product.getPrice(),
        product.getQuantity(),
        product.getStatus()
    );


    when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));
    when(productRepository.findByIdForUpdate(any(Long.class))).thenReturn(product);

    //when
    ProductResponse actualResult = productService.updateProduct(productUpdateRequest,
        "ADMIN");

    //then
    ProductResponse expectResult = product.toDto(product);
    assertThat(actualResult)
        .usingRecursiveComparison()
        .isEqualTo(expectResult);
    verify(productRepository, times(1)).findById(any());

  }

  @Test
  void 상품_삭제_성공() {
    //given

    Field id = ReflectionUtils.findField(Product.class,"id");
    ReflectionUtils.makeAccessible(id);
    ReflectionUtils.setField(id,product,1L);

    when(productRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(product));
    when(productRepository.findByIdForUpdate(any(Long.class))).thenReturn(product);


    //when
    productService.deleteProduct(1L, "ADMIN");

    //then
    assertThat(product.isDeleted())
        .isEqualTo(true);
    verify(productRepository, times(1)).findById(any());
  }


}
