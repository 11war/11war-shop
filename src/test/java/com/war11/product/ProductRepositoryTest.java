package com.war11.product;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.war11.domain.product.dto.request.ProductRequest;
import com.war11.domain.product.dto.request.ProductSaveRequest;
import com.war11.domain.product.entity.Product;
import com.war11.domain.product.entity.enums.ProductStatus;
import com.war11.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@AutoConfigureTestDatabase( replace = AutoConfigureTestDatabase.Replace.NONE )
@DataJpaTest
@DisplayName("Product JPA test")
public class ProductRepositoryTest {

  @Autowired
  private ProductRepository productRepository;

  @Test
  void productRepositoryTest(){
      //given
    final String name = "갤럭시북";
    final String category = "노트북";
    final Long price = 1270000L;
    final int quantity = 15;
    Product product = Product.toEntity(ProductSaveRequest.toDto(new ProductRequest(
        null,
        name,
        category,
        price,
        quantity,
        null)));

    //when
    Product resultProduct = productRepository.save(product);

    //then
    assertThat(resultProduct).isNotNull();
    assertThat(resultProduct.getName()).isEqualTo(name);
    assertThat(resultProduct.getCategory()).isEqualTo(category);
    assertThat(resultProduct.getPrice()).isEqualTo(price);
    assertThat(resultProduct.getQuantity()).isEqualTo(quantity);
    assertThat(resultProduct.getStatus()).isEqualTo(ProductStatus.AVAILABLE);

  }

}
