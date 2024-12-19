package com.landlordpro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.landlordpro.dto.UserDto;
import com.landlordpro.service.UserService;

import jakarta.validation.Valid;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Display registration form
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "register";
    }

    // Handle registration form submission
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserDto userDto,
        BindingResult bindingResult,
        Model model) {

        // Check if there are validation errors
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // Check if the user already exists
        if (userService.isUserExists(userDto.getEmail())) {
            model.addAttribute("error", "An account with this email already exists.");
            return "register";
        }

        // Save the user
        // Encode password before saving
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Assuming saveUser method accepts UserDto, if it accepts User, then convert here
        userService.saveUser(userDto);

        // Redirect to login page with success message
        return "redirect:/login?registered";
    }
}


