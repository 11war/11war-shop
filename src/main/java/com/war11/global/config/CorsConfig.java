package com.war11.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/*
   CORS(Cross-Origin Resource Sharing) 설정을 위한 필터
   - 서로 다른 도메인(예: 백엔드와 프론트엔드)이 통신할 수 있도록 허용하는 역할
   - 특정 출처(origin)에서의 요청만 허용하도록 설정 가능
   - 허용할 HTTP 메서드, 헤더, 인증 정보 등을 정의
   - Spring Security 필터 체인에 등록되어 모든 요청에 CORS 정책 적용
 */

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:8080", "http://localhost:3000")); // 프론트엔드 주소 추가
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization")); // JWT 토큰 허용
        source.registerCorsConfiguration("/**", config);
        source.registerCorsConfiguration("/swagger-ui/**", config);
        source.registerCorsConfiguration("/v3/api-docs/**", config);
        return new CorsFilter(source);
    }
}
