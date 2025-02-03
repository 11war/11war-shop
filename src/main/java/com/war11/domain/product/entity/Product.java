package com.war11.domain.product.entity;

import com.war11.domain.product.dto.request.ProductSaveRequest;
import com.war11.domain.product.dto.request.ProductUpdateRequest;
import com.war11.domain.product.entity.enums.ProductStatus;
import com.war11.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
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

    @Builder
    private Product(String name, String category, Long price, int quantity, String status, boolean isDeleted){
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.status = ProductStatus.from(status);
        this.isDeleted = isDeleted;
    }


    public static Product toEntity(ProductSaveRequest productSaveRequest){
        return Product.builder()
            .name(productSaveRequest.name())
            .category(productSaveRequest.category())
            .price(productSaveRequest.price())
            .quantity(productSaveRequest.quantity())
            .status("available")
            .isDeleted(false)
            .build();
    }

    public void updateProduct(ProductUpdateRequest productUpdateRequest) {
        this.name = productUpdateRequest.name();
        this.category = productUpdateRequest.category();
        this.price = productUpdateRequest.price();
        this.quantity = productUpdateRequest.quantity();
        this.status = ProductStatus.from(productUpdateRequest.status());
    }

    public void deleteProduct(){
        this.isDeleted=true;
    }
}
