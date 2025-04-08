package com.security3.oauth2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.stereotype.Component;

// yml 대신 클래스 객체 생성
@Component
public class SocialClientRegistration {

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

    // 여기서는 구글 provider 우리가 직접 등록해줘야하는데 그냥 아래값 사용하면된다
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