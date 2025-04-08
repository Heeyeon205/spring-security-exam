package com.security3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable);
        // 우리가 직접 로그인 구현 x => 외부(구글,네이버 )에게 위임
        http
                .formLogin(AbstractHttpConfigurer::disable);
        // formLogin disable 하게 되면 자동적으로 popup 로그인 창 나옴 => 해제
        http
                .httpBasic(AbstractHttpConfigurer::disable);
        // 디폴트 outh2Login 창
        http
                .oauth2Login(Customizer.withDefaults());
        http
                .authorizeHttpRequests((auth)->auth
                        .requestMatchers("/", "/oauth2/**" , "/login/**").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }

}
