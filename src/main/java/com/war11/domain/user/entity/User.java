package com.war11.domain.user.entity;

import com.war11.domain.cart.entity.Cart;
import com.war11.domain.order.entity.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Entity
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 12, unique = true)
    private String loginId;

    @Column(length = 30)
    private String name;

    private String password;

    @OneToMany(mappedBy = "user")
    private List<Order> orders = new ArrayList<>();

    @OneToOne(mappedBy = "user")
    private Cart cart;
}
