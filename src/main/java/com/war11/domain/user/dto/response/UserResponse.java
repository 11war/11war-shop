package com.war11.domain.user.dto.response;

import com.war11.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private final Long id;
    private final String loginId;
    private final String name;

    public static UserResponse toDto(User user){
        return new UserResponse(
            user.getId(),
            user.getLoginId(),
            user.getName()
        );
    }
}
