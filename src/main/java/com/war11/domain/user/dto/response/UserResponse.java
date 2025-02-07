package com.war11.domain.user.dto.response;

import com.war11.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String loginId;
    private String name;

    public UserResponse(User foundUser) {
        this.id = foundUser.getId();
        this.loginId = foundUser.getLoginId();
        this.name = foundUser.getName();
    }
}
