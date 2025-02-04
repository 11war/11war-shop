package com.war11.domain.user.dto.response;

import com.war11.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserResponse {
    private final Long id;
    private final String loginId;
    private final String name;

    public UserResponse(User foundUser) {
        this.id = foundUser.getId();
        this.loginId = foundUser.getLoginId();
        this.name = foundUser.getName();
    }
}
