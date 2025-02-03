package com.landlordpro.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.landlordpro.dto.enums.UserRole;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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
    public String loginPage(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object error = session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
            if (error != null) {
                model.addAttribute("errorMessage", error.toString());
                session.removeAttribute("SPRING_SECURITY_LAST_EXCEPTION");
            }
        }
        return "login";
    }
}
