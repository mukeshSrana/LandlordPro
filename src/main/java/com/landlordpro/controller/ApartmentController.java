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
import com.landlordpro.dto.enums.UserRole;
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

    @PostMapping("/add")
    public String add(@ModelAttribute ApartmentDto apartmentDto, Authentication authentication, RedirectAttributes redirectAttributes){
        try {
            CustomUserDetails userDetails = currentUser(authentication);
            // Retrieve the logged-in user's ID
            UUID userId = userDetails.getId();

            apartmentDto.setUserId(userId);

            apartmentService.add(apartmentDto);

            redirectAttributes.addFlashAttribute("successMessage", "Apartment created successfully!");
            //return "registerApartment"; // Redirect or forward to success page
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error occurred: " + e.getMessage());
            log.error("Unexpected error while saving apartment: ", e);
            //return "registerApartment"; // Return to the form with error message
        }
        redirectAttributes.addFlashAttribute("page", "registerApartment");
        return "redirect:/apartment/register";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute ApartmentDto apartmentDto, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            CustomUserDetails userDetails = currentUser(authentication);
            // Retrieve the logged-in user's ID
            UUID userId = userDetails.getId();

            apartmentService.update(apartmentDto, userId);

            redirectAttributes.addFlashAttribute("successMessage", "Apartment updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error occurred: " + e.getMessage());
            log.error("Unexpected error while updating apartment: ", e);
        }
        redirectAttributes.addFlashAttribute("page", "handleApartment");
        return "redirect:/apartment/handle";
    }

    @GetMapping("/register")
    public String register(Model model, Authentication authentication) {
        CustomUserDetails userDetails = currentUser(authentication);

        // Check if user has ROLE_USER or ROLE_ADMIN
        // Check if user has ROLE_USER or ROLE_ADMIN using the UserRole enum
        boolean hasRoleUserOrAdmin = userDetails.getAuthorities().stream()
            .anyMatch(authority ->
                authority.getAuthority().equals(UserRole.ROLE_USER.toString()) ||
                    authority.getAuthority().equals(UserRole.ROLE_ADMIN.toString())
            );
        // Only set ownerName if the user has either ROLE_USER or ROLE_ADMIN
        if (hasRoleUserOrAdmin) {
            model.addAttribute("ownerName", userDetails.getName());
        }

        model.addAttribute("page", "registerApartment");
        return "registerApartment";
    }

    @GetMapping("/handle")
    public String handle(Model model, Authentication authentication) {
        CustomUserDetails userDetails = currentUser(authentication);
        UUID userId = userDetails.getId();
        model.addAttribute("apartments", apartmentService.getApartmentsForUser(userId));
        model.addAttribute("page", "handleApartment");
        return "handleApartment";
    }

    private CustomUserDetails currentUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            return customUserDetails;
        }
        throw new IllegalStateException("Unexpected principal type");
    }
}

