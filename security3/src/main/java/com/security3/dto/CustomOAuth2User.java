package com.security3.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {
    private final OAuth2Response oAuth2Response;
    private final String role;

    // OAuth2Response 안에서 json 파일 객체 핸들링 했음
    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> colleciton = new ArrayList<>();
        colleciton.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return role;
            }
        });

        return colleciton;
    }

    @Override
    public String getName() {
        return oAuth2Response.getName();
    }
    // 외부 서비스에서 로그인 한사람은 username(google_~~) 없고 password(temppassword) 없다 => 우리가 직접 생성 =>
    public String getUsername(){
        return oAuth2Response.getProvider()+"_"+oAuth2Response.getProviderId();
    }
}
