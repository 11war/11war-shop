package com.war11.domain.auth.service;

import com.war11.domain.auth.dto.request.SignupRequest;
import com.war11.domain.auth.dto.response.SignupResponse;
import com.war11.domain.user.dto.request.UserRequest;
import com.war11.domain.user.dto.response.UserResponse;
import com.war11.domain.user.entity.User;
import com.war11.domain.user.repository.UserRepository;
import com.war11.global.exception.BusinessException;
import com.war11.global.exception.enums.ErrorCode;
import com.war11.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;


    public SignupResponse signup(SignupRequest request) {
        if(userRepository.existsByLoginId(request.getLoginId())){
            throw new BusinessException(ErrorCode.NOT_FOUND_ID);
        }

        String encodedPassward = bCryptPasswordEncoder.encode(request.getPassword());

        User newUser = new User(
            request.getLoginId(),
            request.getName(),
            encodedPassward
        );
        userRepository.save(newUser);
        return new SignupResponse("회원가입이 완료되었습니다.");
    }
}
