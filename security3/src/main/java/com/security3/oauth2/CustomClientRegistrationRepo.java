package com.security3.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

import javax.swing.*;

// 구글 객체 ,네이버 객체 => registration 객체
@Configuration
@RequiredArgsConstructor
public class CustomClientRegistrationRepo {
// Google, Naver 등 소셜 로그인 클라이언트 설정을 직접 등록하여, InMemory 방식으로 Spring Security 에 등록하는 설정 클래스

    private final SocialClientRegistration socialClientRegistration;

    @Bean   // ClientRegistrationRepository: Spring Security가 사용하는 소셜 클라이언트 저장소
    public ClientRegistrationRepository clientRegistrationRepository(){
        return new InMemoryClientRegistrationRepository(
                socialClientRegistration.googleClientRegistration() ,
                socialClientRegistration.naverClientRegistration()
        );
    }
}
