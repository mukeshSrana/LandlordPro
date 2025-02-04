package com.landlordpro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.landlordpro.dto.PasswordChangeDto;
import com.landlordpro.dto.UserRegistrationDTO;
import com.landlordpro.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/changePassword")
    public String showChangePasswordForm(@RequestParam(value = "success", required = false) String success,
        @RequestParam(value = "userName", required = false) String userName, Model model) {
        model.addAttribute("passwordChange", new PasswordChangeDto());
        if (success != null) {
            model.addAttribute("successMessage",
                "Password changed, " + userName + "! You can <a href='/login'>click here</a> to log in.");
        }
        return "changePassword";
    }

    @PostMapping("/changePassword")
    public String changePassword(@ModelAttribute PasswordChangeDto passwordChangeDto, Model model, BindingResult result) {
        model.addAttribute("passwordChange", passwordChangeDto);
        if (result.hasErrors()) {
            return "changePassword";
        }

        try {
            userService.changePassword(passwordChangeDto);
            return "redirect:/users/changePassword?success&&userName=" + passwordChangeDto.getUsername();
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "changePassword";
        }
    }

    @GetMapping("/register")
    public String showRegistrationForm(@RequestParam(value = "success", required = false) String success,
        @RequestParam(value = "userName", required = false) String userName, Model model) {
        model.addAttribute("user", new UserRegistrationDTO());

        if (success != null) {
            model.addAttribute("successMessage",
                "Registration successful, " + userName +"! You will receive an email once your account is ready to use.");
        }

        return "registerUser";
    }

    @PostMapping("/register")
    public String registerUser(
        Model model,
        @ModelAttribute("user") @Valid UserRegistrationDTO userDTO,
        @RequestParam("acceptConsent") boolean acceptConsent,
        @RequestParam("acceptTenantDataResponsibility") boolean acceptTenantDataResponsibility,
        BindingResult result) {

        model.addAttribute("registerUser", userDTO);

        if (result.hasErrors()) {
            return "registerUser"; // If validation errors, return to registration page
        }

        if (!acceptConsent || !acceptTenantDataResponsibility) {
            model.addAttribute("errorMessage", "You must accept private-policy/GDPR-consent and acknowledge your responsibility for tenant data.");
            return "registerUser";
        }

        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            model.addAttribute("errorMessage", "Passwords do not match.");
            return "registerUser"; // Reload the form with an error message
        }

        // Check if the user already exists
        if (userService.isUserExists(userDTO.getUsername())) {
            model.addAttribute("errorMessage", "An account with this email already exists.");
            return "registerUser";
        }

        try {
            userService.registerUser(userDTO);

            return "redirect:/users/register?success&&userName=" + userDTO.getName();
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "registerUser";
        }
    }
}


