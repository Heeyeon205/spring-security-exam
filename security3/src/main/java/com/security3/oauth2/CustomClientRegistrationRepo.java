package com.security3.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

// 구글 객체 ,네이버 객체 => registration 객체
@Configuration
@RequiredArgsConstructor
public class CustomClientRegistrationRepo {
    private final SocialClientRegistration socialClientRegistration;

    // 소셜객체가 2개 밖에 없어서 서버 내부 inMemory로 저장
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(){
        return new InMemoryClientRegistrationRepository(
                socialClientRegistration.googleClientRegistration() ,
                socialClientRegistration.naverClientRegistration()

        );
    }
}
