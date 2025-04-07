package com.security2.jwt;

import com.security2.domain.UserEntity;
import com.security2.dto.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor    // 토큰 검사하는 클래스
public class JwtFilter extends OncePerRequestFilter { // 요청이 들어오면 한 번만 작동하는 필터
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // request header 에 암호화된 jwt 토큰이 있다. 이것을 다시 복호화를 해야 한다.
        String authorization = request.getHeader("Authorization"); // Bearer~로 시작

        // 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.info("token null");
            filterChain.doFilter(request, response); // 그 다음 필터 실행해
            return;
        }
        // Bearer 를 빼고 진행
        String token = authorization.split(" ")[1];

        // 토큰 만료 확인
        try {
            if (jwtUtil.isExpired(token)) {
                log.info("Token expired");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token expired");
                return;
            }
        } catch (ExpiredJwtException e) {
            log.info("JWT Exception : {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Expired token");
            return;
        }


        // 토큰값 암호를 복호화해서 값을 꺼내온다.
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        // 시큐리티 세션(stateless)에 값을 UserEntity 객체로 저장한다.
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setRole(role);
        userEntity.setPassword("temp password");

        // userDetails 에 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        // 스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        // 시큐리티 컨텍스트 홀더에 사용자를 등록해야 한다. (시큐리티 세션 객체에 저장)
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
        return;
    }
}
