package com.security2.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Iterator;

@RestController
public class MainController {

    @GetMapping({"","/"})
    public String mainPage() {
        return "main";
    }

    @GetMapping("/auth")
    @ResponseBody
    public String authPage() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities(); // 콜렉션은 인덱스가 없다.
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator(); // 이터레이터로 변경해서 값을 꺼낼 수 있다.
        GrantedAuthority grantedAuthority = iterator.next();
        String role = grantedAuthority.getAuthority();
        return role + " : " + username;
    }
}
