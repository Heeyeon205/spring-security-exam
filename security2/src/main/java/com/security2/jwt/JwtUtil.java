package com.security2.jwt;


import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// 토큰 발행 클래스
@Component
public class JwtUtil {
    private SecretKey secretKey;

    // 환경변수
    public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
        // 암호화, 복호화 작업에 사용 하는 리 서버의 시그니처 키코드 => 개인키
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String createJwt(String username, String role, Long expiredMs) {
        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis())) // 생성일
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) // 만료일
                .signWith(secretKey)    // 웹서버에서 만든 jwt 코드
                .compact(); // 암호화 실행
    }
}