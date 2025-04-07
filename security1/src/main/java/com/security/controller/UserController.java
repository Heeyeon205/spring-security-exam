package com.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/myPage")
public class UserController {

    @GetMapping({"","/"})
    public String myPage() {
        return "/myPage";
    }
}

