package com.landlordpro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String showIndexPage() {
        return "index";  // This will return index.html from the resources/templates directory
    }
}
