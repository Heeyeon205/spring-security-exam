package com.security3.config;


import com.security3.oauth2.CustomClientRegistrationRepo;
import com.security3.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity // Spring Security 기능 활성화
public class SecurityConfig {
// Spring Security 필터 체인을 설정하고, 소셜 로그인(OAuth2) 기반 인증 구조를 구성하는 설정 클래스

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomClientRegistrationRepo customClientRegistrationRepo;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http    // 	CSRF 보호 비활성화 (API, 테스트 목적 등에서 사용)
                .csrf(AbstractHttpConfigurer::disable);

        http    // 기본 제공되는 form 로그인 비활성화 (소셜 로그인 사용)
                .formLogin(AbstractHttpConfigurer::disable);

        http    // 기본 브라우저 팝업 로그인창 비활성화
                .httpBasic(AbstractHttpConfigurer::disable);

//        http    // 기본 OAuth2 로그인 설정 사용 (구글/네이버 로그인)
//                .oauth2Login(Customizer.withDefaults());

        http    // 직접 생성한 CustomOAth2UserService 등록
                .oauth2Login((oauth2) -> oauth2
                        .loginPage("/login")    // 기본 OAuth2 로그인 설정 해제 후 직접 만든 파일 추가
                        .clientRegistrationRepository(customClientRegistrationRepo.clientRegistrationRepository())
                        .userInfoEndpoint((userInfoEndpointConfig ->
                                userInfoEndpointConfig.userService(customOAuth2UserService)
                        ))
                );

        http    // 특정 URL(루트, 로그인 관련)은 모두 허용, 나머지 URL 은 인증 요구
                .authorizeHttpRequests((auth)->auth
                        .requestMatchers("/", "/oauth2/**", "/login").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build(); // SecurityFilterChain 객체를 생성해서 스프링 빈으로 등록
    }
}
