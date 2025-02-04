package com.war11.domain.auth.controller;

import com.war11.domain.auth.dto.request.SigninRequest;
import com.war11.domain.auth.dto.request.SignupRequest;
import com.war11.domain.auth.dto.response.SigninResponse;
import com.war11.domain.auth.dto.response.SignupResponse;
import com.war11.domain.auth.service.AuthService;
import com.war11.global.config.CustomUserDetails;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    /*
    auth의 경우  Entity 형태로 return 받으면 의도치 않은 정보까지 클라이언트에게 노출될 수 있다고 알고 있어서
    보안상의 이유로 일반 Response로 return 처리
     */
    @PostMapping("/auth/signup")
    public SignupResponse signup(@Valid @RequestBody SignupRequest request) {
        return authService.signup(request);
    }

    @PostMapping("/auth/signin")
    public SigninResponse signin(@Valid @RequestBody SigninRequest request) {
        return authService.signin(request);
    }

    @PostMapping("/logout")
    public SignupResponse logout(@RequestHeader("Authorization") String token) {
         return authService.logout(token);
    }

}
