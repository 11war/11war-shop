package com.war11.domain.auth.controller;

import com.war11.domain.auth.dto.request.SigninRequest;
import com.war11.domain.auth.dto.request.SignupRequest;
import com.war11.domain.auth.dto.response.SigninResponse;
import com.war11.domain.auth.dto.response.SignupResponse;
import com.war11.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "회원가입과 로그인입니다.")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/signup")
    public SignupResponse signup(@Valid @RequestBody SignupRequest request) {
        return authService.signup(request);
    }

    @PostMapping("/auth/signin")
    public SigninResponse signin(@Valid @RequestBody SigninRequest  request) {
        return authService.signin(request);
    }

}
