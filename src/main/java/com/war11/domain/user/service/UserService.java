package com.war11.domain.user.service;

import com.war11.domain.user.dto.response.UserResponse;
import com.war11.domain.user.entity.User;
import com.war11.domain.user.repository.UserRepository;
import com.war11.global.exception.BusinessException;
import com.war11.global.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse findById(Long id) {
        User foundUser = userRepository.findById(id).orElseThrow(
            () -> new BusinessException(ErrorCode.NOT_FOUND_ID)
        );
        return UserResponse.toDto(foundUser);
    }
}
