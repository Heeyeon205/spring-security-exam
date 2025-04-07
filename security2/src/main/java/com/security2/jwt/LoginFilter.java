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

    @Override    // 로그인 검증 시 실행하는 매소드
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 클라이언트가 요청한 username, password 를 추출한다.
        String username = obtainUsername(request);
        String password = obtainPassword(request);
        // authenticationManager 에게 값을 전달한다.
        // UsernamePasswordAuthenticationToken 은 즉, dto 객체다
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);
        log.trace("authToken {}", authToken);
        // 우리가 담은 token 을 authenticationManager 에게 전달한다.
        return authenticationManager.authenticate(authToken);
    }

    @Override   // 로그인 성공 시 실행하는 매소드 -> jwt 발행
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        log.info("successfulAuthentication {}", authResult);
        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();
        String username = customUserDetails.getUsername();
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> authoritiesIterator = authorities.iterator();
        GrantedAuthority authority = authoritiesIterator.next();
        String role = authority.getAuthority();
        String token = jwtUtil.createJwt(username, role, 60*60*10L);
        log.info("token {}", token);
        // Bearer 는 한 칸 꼭 띄어쓰기를 해야 한다.
        // 응답 헤더로 키 : Authorization 벨류 : Bearer  + token (프론트 서버에 보내줌)
        response.addHeader("Authorization", "Bearer " + token);
    }

    @Override   // 로그인 실패 시 실행하는 매소드
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
       response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 에러코드
       log.info("unsuccessfulAuthentication {}", failed);
    }
}
