package com.landlordpro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.landlordpro.dto.PasswordChangeDto;
import com.landlordpro.dto.UserRegistrationDTO;
import com.landlordpro.service.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/changePassword")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("user", new UserRegistrationDTO());
        return "changePassword";
    }

    @PostMapping("/changePassword")
    public String changePassword(
        @Valid @ModelAttribute("passwordChange") PasswordChangeDto passwordChangeDto,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("passwordChange", passwordChangeDto);
            return "changePassword";
        }

        try {
            userService.changePassword(passwordChangeDto);
            redirectAttributes.addFlashAttribute("successMessage",
                "Password changed, " + passwordChangeDto.getUsername() + "! You can <a href='/login'>click here</a> to log in.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            log.error(e.getMessage(), e);
        }
        return "redirect:/users/changePassword";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDTO());
        return "registerUser";
    }

    @PostMapping("/register")
    public String registerUser(
        @Valid @ModelAttribute("user") UserRegistrationDTO userDTO,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            // Return to the form with error messages
            model.addAttribute("user", userDTO);
            return "registerUser";
        }

        if (!userDTO.isAcceptConsent() || !userDTO.isAcceptTenantDataResponsibility()) {
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
            redirectAttributes.addFlashAttribute("successMessage",
                "Registration successful, " + userDTO.getUsername() + "! You will receive an email once your account is ready to use.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            log.error(e.getMessage(), e);
        }
        return "redirect:/users/register";
    }
}


