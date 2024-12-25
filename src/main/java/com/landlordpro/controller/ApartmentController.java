package com.landlordpro.controller;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.landlordpro.dto.ApartmentDto;
import com.landlordpro.security.CustomUserDetails;
import com.landlordpro.service.ApartmentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/apartment")
public class ApartmentController {
    private final ApartmentService apartmentService;

    public ApartmentController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @PostMapping("/save")
    public String save(@ModelAttribute ApartmentDto apartmentDto, Authentication authentication, Model model) {
        try {
            // Retrieve the logged-in user's ID
            UUID userId = currentUserId(authentication);

            apartmentDto.setUserId(userId);

            // Save the apartment to the database
            apartmentService.save(apartmentDto);

            model.addAttribute("successMessage", "Apartment created successfully!");
            return "registerApartment"; // Redirect or forward to success page
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Unexpected error occurred: " + e.getMessage());
            log.error("Unexpected error while saving apartment: ", e);
            return "registerApartment"; // Return to the form with error message
        }
    }

    @PostMapping("/update")
    public String update(@ModelAttribute ApartmentDto apartmentDto, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            // Retrieve the logged-in user's ID
            UUID userId = currentUserId(authentication);

            // Save the apartment to the database
            apartmentService.update(apartmentDto, userId);

            redirectAttributes.addFlashAttribute("successMessage", "Apartment updated successfully!");
            return "redirect:/apartment/handle"; // Redirect or forward to success page
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error occurred: " + e.getMessage());
            log.error("Unexpected error while updating apartment: ", e);
            return "redirect:/apartment/handle"; // Return to the form with error message
        }
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("page", "registerApartment");
        return "registerApartment";
    }

    @GetMapping("/handle")
    public String handle(Model model, Authentication authentication) {
        UUID userId = currentUserId(authentication);
        model.addAttribute("apartments", apartmentService.getApartmentsForUser(userId));
        model.addAttribute("page", "handleApartment");
        return "handleApartment";
    }

    public UUID currentUserId(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getId();
        }
        throw new IllegalStateException("Unexpected principal type");
    }
}

