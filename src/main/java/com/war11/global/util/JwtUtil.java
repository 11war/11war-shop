package com.war11.global.util;

import com.war11.domain.user.entity.User;
import com.war11.global.exception.BusinessException;
import com.war11.global.exception.enums.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtUtil {

    private static final long TOKEN_TIME = 60 * 60 * 1000L; //"${jwt.secret.key}")
    private static final String BEARER_PREFIX = "Bearer ";
    public static final Set<String> EXPIRED_TOKEN_SET = new HashSet<>();

    @Value("${jwt.secret.key}")
    private String secretkey;

    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    /*
    Todo: 공부를 위한 메모
    secretKey는 openssl로 HS256 랜덤키 생성 후 base64로 변환해 상태이다.
    따라서 아래의 초기화를 사용해 디코딩 과정이 필요하다.
    PostConstruct를 사용하면 의존성 주입 후에 초기화를 진행하여 객체의 값을 설정할 수있다.
    이걸 사용하지 않으면 Spring bean이 초기화되지 않음.
     */
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretkey.getBytes());
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String creatToken(User user) {
        Date date = new Date(); //현재 날짜와 시간을 나타내는 date 생성자

        /*
        todo:
        jwt.builder() 메서드로 JwtBuilder 인스턴스를 만들고,
        JwtBuilder 메서드를 호출하여 header 파라미터와 clames를 등록한다.
        jwt의 서명에 사용할 비밀키를 지정하고, compact() 메서드를 호출하여 jwt를 생성
         */
        return BEARER_PREFIX +
            Jwts.builder()
                .setSubject(String.valueOf(user.getId())) //이 토큰을 어따가 왜 쓸건지? 기준설정
                .setId(String.valueOf(user.getId()))
                .claim("loginId", user.getLoginId()) //토큰에 상세한 값 추가
                .claim("name", user.getName())
                .setExpiration(
                    new Date(date.getTime() + TOKEN_TIME)) //토큰 만료 시간을 발급시간으로부터 상수 TOKEN_TIME으로 결정
                .setIssuedAt(date) // 발급시간
                .signWith(key, signatureAlgorithm) // 비밀키와 알고리즘 유형 설정
                .compact();
    }

    /*
    Token 앞에 있는 Bearer을 잘라내서 순수 토큰으로 return 하기 위한 메서드
    상수  BEARER_PREFIX 안 쓰려고 했는데 두 개나 쓰고 있길래 그냥 씀
     */
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7); // 7번째 인덱스부터 잘라
        }
        throw new BusinessException(ErrorCode.NOT_FOUND_TOKEN);
        //포스트맨 노출 안됨. 고쳐보기.
    }

    /*
    토큰에 있는 값을 파싱해서 가져오기 위해 extractClaims 메서드 사용
    todo: 이 부분을 더 공부하라. 정확한 이해가 추가로 더 필요하다.
     */

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder() //jwt 파서를 생성할 수 있는 builder 객체
            .setSigningKey(key) //서명검증에 사용할 키를 설정, 이 때 위에서 설정한대로 디코딩 되어 사용
            .build() // jwt 파서 객체를 생성
            .parseClaimsJws(token) //jwt 문자열을 이용하여 header, claims, signature가 포함된 객체 생성
            .getBody(); // 해당 객체를 반환
    }
}
