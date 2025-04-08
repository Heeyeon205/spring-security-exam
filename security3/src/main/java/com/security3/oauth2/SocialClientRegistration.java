package com.security3.oauth2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.stereotype.Component;

import javax.swing.*;

// yml 대신 클래스 객체 생성
@Component
public class SocialClientRegistration {
// Google, Naver 로그인 정보를 코드 기반으로 등록하는 설정 클래스
// ClientRegistration 객체를 직접 생성해서 Spring Security 에 넘겨주는 역할

    private final String googleId;
    private final String googleKey;
    private final String naverId;
    private final String naverKey;

    public SocialClientRegistration(@Value("${google.client-id}") String googleId,
                                    @Value("${google.client-key}") String googleKey,
                                    @Value("${naver.client-id}") String naverId,
                                    @Value("${naver.client-key}") String naverKey) {
        this.googleId = googleId;
        this.googleKey = googleKey;
        this.naverId = naverId;
        this.naverKey = naverKey;
    }

    // Naver OAuth2 로그인 설정 객체 생성
    public ClientRegistration naverClientRegistration() {
        return ClientRegistration.withRegistrationId("naver")
                .clientId(naverId)
                .clientSecret(naverKey)
                .redirectUri("http://localhost:8080/login/oauth2/code/naver")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope("name", "email")
                .authorizationUri("https://nid.naver.com/oauth2.0/authorize")
                .tokenUri("https://nid.naver.com/oauth2.0/token")
                .userInfoUri("https://openapi.naver.com/v1/nid/me")
                .userNameAttributeName("response")
                .build();
    }

    // Google OAuth2 로그인 설정 객체 생성
    public ClientRegistration googleClientRegistration() {
        return ClientRegistration.withRegistrationId("google")
                .clientId(googleId)
                .clientSecret(googleKey)
                .redirectUri("http://localhost:8080/login/oauth2/code/google")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope("profile", "email")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .issuerUri("https://accounts.google.com")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .build();
    }
}

/*
application.yml 없이 ID, Secret 등은 환경변수(@Value)로 받음
  ↓
각 소셜 로그인 설정을 직접 ClientRegistration 으로 생성
  ↓
CustomClientRegistrationRepo 에서 InMemory 저장소로 등록
  ↓
Spring Security 가 이 정보를 사용하여 소셜 로그인 처리
 */