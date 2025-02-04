package com.war11.global.config;

import static com.war11.global.util.JwtUtil.EXPIRED_TOKEN_SET;

import com.war11.domain.auth.service.CustomUserDetailsService;
import com.war11.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // JWT는 Stateless로 사용되기 때문에 csrf 공격에 대한 보호가 필요치 않다. 따라서 해당 기능을 비활성화한다.
        http
            .csrf((csrf) -> csrf.disable());

        /*
        Jwt 토큰을 이용한 로그인을 할 것이므로 폼 로그인 기능 비활성화
        httpBasic 또한 Authorization에 아이디와 비밀번호를 base64로 인코딩해서 가져오는 기능으로 보안에 취약하다.
        하지만 이 프로젝트는 Authorization에 jwt 토큰 값을 가져와서 사용할 것이므로 비활성화한다.
        */

        http
            .formLogin((form) -> form.disable())
            .httpBasic((basic) -> basic.disable());

        http
            .authorizeHttpRequests((auth) -> auth
                .requestMatchers("/", "/auth/signup", "/auth/signin","/logout").permitAll() // 화이트리스트
                .requestMatchers("/admin").hasRole("ADMIN") // 관리자 경로 권한 설정
                .anyRequest().authenticated()); // 나머지 요청은 인증 필요

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


        http
            .addFilterAt(new JwtFilter(jwtUtil, userDetailsService), UsernamePasswordAuthenticationFilter.class);

        // 세션 관리를 할 때 JWT 토큰 사용 시에는 Stateless 로 설정해야 하기 때문에 해당 부분 작성
        http
            .sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
