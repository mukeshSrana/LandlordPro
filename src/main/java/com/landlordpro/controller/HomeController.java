package com.landlordpro.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String homePage(Model model, Authentication authentication) {
        return "home";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Return login.html page
    }
}
