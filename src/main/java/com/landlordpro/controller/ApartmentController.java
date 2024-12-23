package com.landlordpro.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.landlordpro.config.AppConfig;
import com.landlordpro.dto.ApartmentDto;
import com.landlordpro.security.CustomUserDetails;
import com.landlordpro.service.ApartmentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/apartment")
public class ApartmentController {
    private final AppConfig appConfig;
    private final ApartmentService apartmentService;

    public ApartmentController(AppConfig appConfig, ApartmentService apartmentService) {
        this.appConfig = appConfig;
        this.apartmentService = apartmentService;
    }

    @PostMapping("/save")
    public String saveApartment(@ModelAttribute ApartmentDto apartmentDto, Authentication authentication, Model model) {
        try {
            // Retrieve the logged-in user's ID
            UUID userId = ((CustomUserDetails) authentication.getPrincipal()).getId();

            // Check if an apartment with the same name already exists for the user
            if (apartmentService.isExistsForUser(apartmentDto.getApartmentShortName(), userId)) {
                model.addAttribute("errorMessage", "Apartment with this name already exists for the logged-in user.");
                return "registerApartment"; // Return to the form with error message
            }

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

    @GetMapping("/register")
    public String registerApartment(Model model) {
        model.addAttribute("page", "registerApartment");
        return "registerApartment";
    }

    @GetMapping("/handle")
    public String handleApartment(Model model, Authentication authentication) {
        model.addAttribute("apartments", apartmentForLoggedInUser(authentication));
        model.addAttribute("page", "handleApartment");
        return "handleApartment";
    }

    private List<ApartmentDto> apartmentForLoggedInUser(Authentication authentication) {
        UUID userId = currentUserId(authentication);
        return apartmentService.getApartmentsForUser(userId);
    }

    public UUID currentUserId(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getId();
        }
        throw new IllegalStateException("Unexpected principal type");
    }
}

