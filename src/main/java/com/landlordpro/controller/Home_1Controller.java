package com.landlordpro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Home_1Controller {
    @GetMapping( "/")
    public String index() {
        return "home";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
