package com.security3.dto;

import java.util.Map;

public class NaverResponse implements OAuth2Response{

    private final Map<String, Object> attribute;  // 네이버에서 제공 JSON

    public NaverResponse(Map<String, Object> attribute) {
        this.attribute = (Map<String, Object>)attribute.get("response");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString(); // 네이버에서 해당유저 고유 클라이언트 id
    }

    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }

    @Override
    public String getName() {
        return attribute.get("name").toString();
    }
}
