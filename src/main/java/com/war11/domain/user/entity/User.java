package com.war11.domain.user.entity;

import com.war11.domain.cart.entity.Cart;
import com.war11.domain.order.entity.Order;
import jakarta.persistence.*;
import lombok.Builder;
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

    /*
    todo: Builder 패턴의 적용 방법이 전체를 적용하는 생성자를 쓰거나 @AllArgsConstructor를 쓰라는데 그렇다 해서 서비스 단위에서 적용이 될 거 같지가 않습니다.
    현재 특정 메서드 안에서 User 타입의 객체에 User.Builder() 형식으로 저장해서 쓰고 있어서 그런거 같아요.
    authService 35~39번째
    엔티티 안에 정적 팩토리 메서드를 작성해서 사용해야 하는건지?
     */
    @Builder
    public User(String loginId, String name, String password) {
        this.loginId = loginId;
        this.name = name;
        this.password = password;
    }
}
