package com.landlordpro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.landlordpro.dto.UserRegistrationDTO;
import com.landlordpro.service.UserService;

import jakarta.validation.Valid;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(Model model, @ModelAttribute("user") @Valid UserRegistrationDTO userDTO, BindingResult result) {
        if (result.hasErrors()) {
            return "register"; // If validation errors, return to registration page
        }

        // Check if the user already exists
        if (userService.isUserExists(userDTO.getUsername())) {
            model.addAttribute("error", "An account with this email already exists.");
            return "register";
        }

        try {
            userService.registerUser(userDTO);
            model.addAttribute("successMessage", "You've successfully registered as " + userDTO.getUsername() + ". Login <a href='/login'>here</a>.");

            return "register";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}


