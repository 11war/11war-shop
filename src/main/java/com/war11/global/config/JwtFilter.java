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
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/*
SpringSecurity 사용을 위해 OncePerRequestFilter를 extend 함.
 */
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private RequestMatcher whitList = new AntPathRequestMatcher("/auth/**");

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        //AntPathRequestMatcher를 사용하여 url에 대한 프리픽스를 만듦, auth가 아닌 것만 체크
        if(whitList.matches(request)) {
            chain.doFilter(request, response);
            return;
        }

        String bearerJwt = request.getHeader("Authorization");

        /*
        빈 문자열일 때나 null일 때를 검사하기 위해 isBlank 사용
         */
        if(bearerJwt.isBlank()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_TOKEN);
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
            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(loginId);

            //인증된 사용자 정보와 비밀번호(jwt에서는 사용안하므로 null), 사용자 권한정보를 매개변수로 하여 객체 생성
            Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());

            //인증된 사용자 정보를 저장
            SecurityContextHolder.getContext().setAuthentication(authToken);
            chain.doFilter(request,response);

            /*
            얘는 맘에들지 않음, 뭔가 다른 방식으로 고쳐보자.
             */
        } catch (SecurityException | MalformedJwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않는 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }
}
