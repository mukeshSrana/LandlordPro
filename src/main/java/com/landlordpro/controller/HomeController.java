package com.landlordpro.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.landlordpro.security.CustomUserDetails;

@Controller
public class HomeController {

    @GetMapping("/")
    public String homePage(Model model, Authentication authentication) {
        // Get the logged-in user's details
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = userDetails.getName();  // Or you can use userDetails.getId() to fetch the user ID

        // Add the user's name to the model
        model.addAttribute("username", username);

        return "home"; // Return the Thymeleaf view (home.html)
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
