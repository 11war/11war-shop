package com.war11.domain.user.service;

import com.war11.domain.auth.service.AuthService;
import com.war11.domain.user.dto.request.UserRequest;
import com.war11.domain.user.dto.response.UserResponse;
import com.war11.domain.user.entity.User;
import com.war11.domain.user.repository.UserRepository;
import com.war11.global.common.ApiResponse;
import com.war11.global.config.CustomUserDetails;
import com.war11.global.exception.BusinessException;
import com.war11.global.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService;

    public UserResponse findById(Long id) {
        User foundUser = userRepository.findById(id).orElseThrow(
            () -> new BusinessException(ErrorCode.NOT_FOUND_ID)
        );
        return new UserResponse(foundUser);
    }

    public Page<UserResponse> findAllUsers(CustomUserDetails userDetails, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<UserResponse> AllUsers =  userRepository.findAllUsers(userDetails.getId(), pageable);

        return  AllUsers.map (User -> new UserResponse(
            userDetails.getId(),
            userDetails.getLoginId(),
            userDetails.getUsername()
        ));
    }

    @Transactional
    public UserResponse updateUsers(CustomUserDetails userDetails, UserRequest request) {
        User user = userRepository.findById(userDetails.getId()).orElseThrow(
            () -> new BusinessException(ErrorCode.NOT_FOUND_ID));
        return user.toDto(request, userDetails);
    }
}
