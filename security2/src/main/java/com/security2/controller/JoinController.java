package com.security2.controller;


import com.security2.dto.JoinLoginRequest;
import com.security2.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class JoinController {
    private final UserService userService;

    @PostMapping("/join")
    @ResponseBody
    public String joinProc(JoinLoginRequest request) {
        log.trace("request : {}", request.getUsername() + " " + request.getPassword());
        try{
            userService.addUser(request);
        }catch(Exception e){
            return e.getMessage();
        }
        return "ok";
    }
}
