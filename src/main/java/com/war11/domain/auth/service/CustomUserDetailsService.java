package com.war11.domain.auth.service;

import com.war11.domain.user.entity.User;
import com.war11.domain.user.repository.UserRepository;
import com.war11.global.config.CustomUserDetails;
import com.war11.global.exception.BusinessException;
import com.war11.global.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) {

        User user = userRepository.findByLoginId(loginId).orElseThrow(
            () -> new BusinessException(ErrorCode.NOT_FOUND_ID));

        return new CustomUserDetails(user);
    }

}
