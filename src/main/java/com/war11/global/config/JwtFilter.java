package com.war11.global.config;

import com.war11.domain.auth.service.CustomUserDetailsService;
import com.war11.global.exception.BusinessException;
import com.war11.global.exception.enums.ErrorCode;
import com.war11.global.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Comment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/*
SpringSecurity 사용을 위해 OncePerRequestFilter를 extend 함.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final RequestMatcher whitList = new AntPathRequestMatcher("/auth/**");
    private final List<String> swaggerWhitelist = List.of(
        "/swagger-ui/**",  // 모든 swagger-ui 하위 경로 포함
        "/swagger-ui",
        "/swagger-ui/index.html",
        "/swagger-ui.html",
        "/v3/api-docs",
        "/v3/api-docs/",
        "/v3/api-docs/swagger-config"
    );

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String url = request.getRequestURI();
        log.info("Request URI: {}", url);

        //AntPathRequestMatcher를 사용하여 url에 대한 프리픽스를 만듦, auth가 아닌 것만 체크
        if(whitList.matches(request) || swaggerWhitelist.stream().anyMatch(url::startsWith)) {
            chain.doFilter(request, response);
            return;
        }

        String bearerJwt = request.getHeader("Authorization");

        /*
        빈 문자열일 때나 null일 때를 검사하기 위해 isBlank 사용
         */
        if( bearerJwt == null || bearerJwt.isBlank()) {
            log.warn("JWT 토큰 없음");
            throw new BusinessException(ErrorCode.NOT_FOUND_TOKEN);
        }

        if (JwtUtil.EXPIRED_TOKEN_SET.contains(bearerJwt)) {
            log.warn("로그아웃된 JWT 토큰");
            sendError(response,HttpServletResponse.SC_FORBIDDEN,"이미 로그아웃 되었습니다.");
            return;
        }

        try {
            Claims claims = jwtUtil.extractClaims(jwtUtil.substringToken(bearerJwt));
            if(claims == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_TOKEN);
            }

            /*
            스프링 시큐리티에서 기본으로 사용자를 찾는 기준인 username 대신 loginId를 기준으로 찾기 위해 CustomUserDetailsService 설정
             */
            String loginId = claims.get("loginId", String.class);
            String role = claims.get("role", String.class);  // role 정보 가져오기

            // "ROLE_"이 중복으로 붙는 경우 방지
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }


            if (loginId == null || role == null) {
                log.error("JWT에 필수 정보 없음 (loginId={}, role={})", loginId, role);
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "JWT에 필요한 정보가 없습니다.");
                return;
            }
            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(loginId);

            //인증된 사용자 정보와 비밀번호(jwt에서는 사용안하므로 null), 사용자 권한정보를 매개변수로 하여 객체 생성
            Authentication authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, List.of(new SimpleGrantedAuthority(role))
            );

            log.info("현재 사용자 권한: {}", userDetails.getAuthorities());

            //인증된 사용자 정보를 저장
            SecurityContextHolder.getContext().setAuthentication(authToken);
            chain.doFilter(request,response);

        } catch (SecurityException | MalformedJwtException e) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않는 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.");
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "그 외의 오류입니다..");
        }
    }

    //postman에서 확인할 수 있게 json 형태로 출력하는 메서드
    private void sendError (HttpServletResponse response,int errorCode, String msg) throws IOException {
        response.setStatus(errorCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(msg);
        response.getWriter().flush();
    }
}
