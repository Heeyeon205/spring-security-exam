package com.security3.dto;

public interface OAuth2Response {
    // 제공자 => google, name
    String getProvider();
    // 제공자에서 발급받은 아이디(번호)
    String getProviderId();
    // 이메일
    String getEmail();
    // 이름
    String getName();
}
