package com.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration  // 이 클래스가 스프링 설정 클래스임을 명시. Bean 등록용
@EnableWebSecurity  // Spring Security 활성화. 이 클래스에서 보안 설정을 하겠다는 의미
public class SecurityConfig {

    // 패스워드 암호화 객체
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 보안 필터 체인을 구성하는 핵심 메서드. 이 Bean이 Security 설정의 중심
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http    // csrf 비활성화
//                .csrf(AbstractHttpConfigurer::disable);

        http    // Spring Security 6에서 HTTP 요청에 대한 접근 제어(Authorization) 를 구성할 때 사용하는 메서드.
                .authorizeHttpRequests((authorize) ->  authorize
                .requestMatchers("/", "/join", "/joinProc", "/login", "/loginProc").permitAll()    // 루트 URL은 모두 접근 허용
                .requestMatchers("/admin/**").hasRole("ADMIN")     // admin 권한 사용자만 이용 가능
                        .requestMatchers("/mypage/**").hasAnyRole("ADMIN", "USER") // admin, user 일때만 mypage 하위 페이지 접근 가능
                .anyRequest().authenticated()); // 다른 모든 url 은 권한 검증을 거쳐야 한다.

        http
                .formLogin((auth) -> auth
                        .loginPage("/login")    // 개발자가 만든 loginPage
                        .loginProcessingUrl("/loginProc")   // form post => 명시한 url 들어감
                        .permitAll()
                );

        http
                .logout((auth) -> auth.logoutUrl("/logout")
                        .logoutSuccessUrl("/"));

        http    // 세션 고정 공력 보호 (로그인 마다 세션 ID 값을 변경한다.)
                .sessionManagement((auth) -> auth
                        .sessionFixation().changeSessionId());

        return http.build();
    }
}
