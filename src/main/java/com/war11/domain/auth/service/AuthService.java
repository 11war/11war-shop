package com.war11.domain.auth.service;

import com.war11.domain.auth.dto.request.SigninRequest;
import com.war11.domain.auth.dto.request.SignupRequest;
import com.war11.domain.auth.dto.response.SigninResponse;
import com.war11.domain.auth.dto.response.SignupResponse;
import com.war11.domain.user.entity.User;
import com.war11.domain.user.repository.UserRepository;
import com.war11.global.exception.BusinessException;
import com.war11.global.exception.enums.ErrorCode;
import com.war11.global.util.JwtUtil;
import jakarta.validation.Valid;
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
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new BusinessException(ErrorCode.ID_ALREADY_EXISTS);
        }

        String encodedPassward = bCryptPasswordEncoder.encode(request.getPassword());

        User newUser = User.builder()
            .loginId(request.getLoginId())
            .name(request.getName())
            .password(encodedPassward)
            .build();
        userRepository.save(newUser);
        return new SignupResponse("회원가입이 완료되었습니다.");
    }

    public SigninResponse signin(SigninRequest request) {
        User user = userRepository.findByLoginId(request.getLoginId()).orElseThrow(
            () -> new BusinessException(ErrorCode.USER_ID_ERROR));

        if(!bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.USER_PW_ERROR);
        }

        String token = jwtUtil.creatToken(user);

        return new SigninResponse(token,"로그인에 성공했습니다.");
    }
}
