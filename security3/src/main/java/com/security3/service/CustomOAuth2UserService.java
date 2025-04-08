package com.security3.service;


import com.security3.domain.UserEntity;
import com.security3.dto.CustomOAuth2User;
import com.security3.dto.GoogleResponse;
import com.security3.dto.NaverResponse;
import com.security3.dto.OAuth2Response;
import com.security3.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    //DefaultOAuth2UserService OAuth2UserService의 구현체

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {

            return null;
        }
        // username => 사용자 입력 ID 가 없으므로 소셜서비스 + 클라이언트id(같은유저면 같은 값을 가지고옴 ) 조합으로 생성
        String username = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();
        UserEntity existData = userRepository.findByUsername(username);

        String role = "ROLE_USER";
        if (existData == null) { // 처음에 들어올때
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(username);
            userEntity.setEmail(oAuth2Response.getEmail());
            userEntity.setRole(role);
            userEntity.setName(oAuth2Response.getName());
            userEntity.setPassword("temppassword");

            userRepository.save(userEntity); // 우리 db 저장
        } else {

            existData.setUsername(username); // 두번째 들어오는 값이면
            existData.setEmail(oAuth2Response.getEmail()); // 다시 들어온 값으로 email 수정

            role = existData.getRole();

            userRepository.save(existData);
        }

        return new CustomOAuth2User(oAuth2Response, role);
    }
}

