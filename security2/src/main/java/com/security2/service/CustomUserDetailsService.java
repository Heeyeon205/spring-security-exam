package com.security2.service;

import com.security.domain.UserEntity;
import com.security.dto.CustomUserDetails;
import com.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    // security 인증 매너지너 클래스가 해당 userDetails 클래스를 다루기 위해 사용하는 서비스 클래스 객체
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username);
        if(user != null){
            return new CustomUserDetails(user);
        }
        return null;
    }
}
