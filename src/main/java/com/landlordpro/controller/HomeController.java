package com.landlordpro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.landlordpro.dto.enums.UserRole;

@Controller
public class HomeController {

    @GetMapping("/")
    public String homePage(Model model) {
        String userRole = model.getAttribute("userRole").toString();
        if (userRole.equals(UserRole.ROLE_LANDLORD.getDescription())) {
            return "homeForLandlord";
        }
        return "homeForAdmin";
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
