package com.war11.global.config;

import com.war11.domain.auth.service.CustomUserDetailsService;
import com.war11.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.io.IOException;

import static com.war11.global.util.JwtUtil.EXPIRED_TOKEN_SET;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final JwtFilter jwtFilter; // JwtFilter를 주입받습니다.
    private final CustomUserDetailsService userDetailsService;


    public SecurityConfig(JwtUtil jwtUtil, JwtFilter jwtFilter, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 기본 보안 설정
        http
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .csrf(csrf -> csrf.disable());

        // 🔹 인증 및 권한 설정
        http
            .authorizeHttpRequests(auth -> auth
                // 🔹 Swagger & API 문서 접근 허용
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                // 🔹 로그인 & 회원가입 API는 인증 없이 접근 가능
                .requestMatchers("/auth/signup", "/auth/signin", "/logout").permitAll()

                // 🔹 관리자 전용 (ROLE_ADMIN 필요)
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")

                // 🔹 나머지 요청은 USER 또는 ADMIN 권한 필요
                .anyRequest().hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
            );

        // 🔹 JWT 로그아웃 처리
        http
            .logout(logout -> logout
                .logoutUrl("/logout")
                .addLogoutHandler((request, response, authentication) -> {
                    String token = jwtUtil.substringToken(request.getHeader("Authorization"));

                    if (token != null && EXPIRED_TOKEN_SET.contains(token)) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setCharacterEncoding("UTF-8");
                        try {
                            response.getWriter().write("이미 로그아웃된 토큰입니다.");
                            return;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    if (token != null) {
                        EXPIRED_TOKEN_SET.add(token);
                    }
                })
                .logoutSuccessHandler((request, response, authentication) -> {
                    if (response.getStatus() == HttpServletResponse.SC_UNAUTHORIZED) {
                        return;
                    }

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("로그아웃이 성공했습니다.");
                })
            );

        // 🔹 JWT 필터 추가
        http
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // 🔹 세션 설정: JWT 사용 시 STATELESS 모드 유지
        http
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
