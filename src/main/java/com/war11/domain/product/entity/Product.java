package com.war11.domain.product.entity;

import com.war11.domain.product.dto.request.ProductSaveRequest;
import com.war11.domain.product.dto.request.ProductUpdateRequest;
import com.war11.domain.product.dto.response.ProductResponse;
import com.war11.domain.product.entity.enums.ProductStatus;
import com.war11.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 60)
    private String name;

    @Column(length = 30)
    private String category;

    private Long price;

    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    private boolean isDeleted;







  public ProductResponse toDto(Product product){
    return ProductResponse.builder()
        .id(product.getId())
        .name(product.getName())
        .category(product.getCategory())
        .price(product.getPrice())
        .quantity(product.getQuantity())
        .status(product.getStatus())
        .createdAt(product.getCreatedAt())
        .updatedAt(product.getUpdatedAt())
        .build();
  }

    public void updateProduct(ProductUpdateRequest productUpdateRequest) {
        this.name = productUpdateRequest.name();
        this.category = productUpdateRequest.category();
        this.price = productUpdateRequest.price();
        this.quantity = productUpdateRequest.quantity();
        this.status = productUpdateRequest.status();
    }

    public void deleteProduct(){
        this.isDeleted=true;
    }

    public void upToQuantity(int upQuantity){
    this.quantity = quantity + upQuantity;
    }

    public void downToQuantity(int downQuantity){
    this.quantity = quantity - downQuantity;
    }
}
