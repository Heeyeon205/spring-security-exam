package com.security2.jwt;

import com.security2.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Slf4j
@RequiredArgsConstructor    // 스프링 시큐리티에서 폼 로그인을 처리하는 핵심 필터.
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    // 로그인 검증 메서드
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        // 클라이언트가 요청한 username, userpassword 추출
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        // authenticationManager값 전달
        // UsernamePasswordAuthenticationToken => dto 객체
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);
        log.info("attemptAuthentication authToken = : {}", authToken);

        // 우리가 담은 token authenticationManager 전달
        return authenticationManager.authenticate(authToken);
    }

    // 로그인 성공시에 실행하는 메서드 -> jwt 발행
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        log.info("successful Authentication ");
        // Authentication 인증객체 -> Principal객체 안에 -> UserDetils 객체 안에
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = customUserDetails.getUsername();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority(); // ROLE_USER , ROLE_ADMIN
        log.info("role : {} ", role);
        String token = jwtUtil.createJwt(username, role, 60 * 60 * 1000L); // 1시간
        log.info("token : {}", token);
        // Bearer 한칸 띄어쓰기 필수~~!!!!
        // 응답해더에 Authorization 키:  Bearer :JWT 토큰 스트링(값)
        response.addHeader("Authorization", "Bearer " + token);
    }

    // 로그인 실패시 실행하는 메서드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 에러 코드 발생
        log.info("unsuccessful Authentication ");

    }
}