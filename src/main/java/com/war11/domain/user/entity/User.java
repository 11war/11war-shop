package com.war11.domain.user.entity;

import com.war11.domain.cart.entity.Cart;
import com.war11.domain.order.entity.Order;
import jakarta.persistence.*;
import javax.management.relation.Role;
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

    private String role;  // 권한 필드 추가

    private String password;

    /*
    빌더패턴으로 생성된 객체를 수정하기 위해서는 toBuiler를 사용해야
    새로 생성되는 게 아니라 기존 값이 고쳐진다.
     */
    @Builder(toBuilder = true)
    public User(String loginId, String name, String password, String role) {
        this.loginId = loginId;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    //아이디와 식별자는 수정불가, 이름과 비밀번호 수정 가능
   public String updateName(String newName){
        return this.name = newName;
   }

   public String updatePassword(String password){
        return this.password = password;
   }

}

