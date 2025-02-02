package com.war11.domain.product.entity;

import com.war11.domain.product.entity.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Product {
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
}
