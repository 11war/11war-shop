package com.war11.domain.product.entity;

import com.war11.domain.product.dto.request.ProductSaveRequest;
import com.war11.domain.product.entity.enums.ProductStatus;
import com.war11.global.common.BaseTimeEntity;
import jakarta.persistence.*;
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

    private Product(String name, String category, Long price, int quantity, String status){
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.status = ProductStatus.from(status);
        isDeleted = false;
    }


    public static Product toEntity(ProductSaveRequest productSaveRequest){
        return new Product(
            productSaveRequest.name(),
            productSaveRequest.category(),
            productSaveRequest.price(),
            productSaveRequest.quantity(),
            "available");
    }


}
