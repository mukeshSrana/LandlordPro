package com.landlordpro.controller;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.landlordpro.dto.ApartmentDto;
import com.landlordpro.security.CustomUserDetails;
import com.landlordpro.service.ApartmentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/apartment")
@PreAuthorize("hasRole('ROLE_LANDLORD')")
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
        // Add query parameters for year and apartmentId
        Integer year = apartmentDto.getCreatedDate().getYear();
        redirectAttributes.addFlashAttribute("page", "handleApartment");
        return "redirect:/apartment/handle?year=" + year;
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") UUID id,
        @RequestParam("userId") UUID userId,
        @RequestParam("year") Integer year,
        Authentication authentication,
        RedirectAttributes redirectAttributes) {
        try {
            CustomUserDetails userDetails = currentUser(authentication);
            // Retrieve the logged-in user's ID
            if (userDetails.getId().equals(userId)) {
                apartmentService.delete(id, userId);
            } else {
                throw new RuntimeException("Logged in userId is not same as the deleted apartment userId");
            }
            redirectAttributes.addFlashAttribute("successMessage", "Apartment deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error occurred: " + e.getMessage());
            log.error("Unexpected error while deleting apartment: ", e);
        }
        redirectAttributes.addFlashAttribute("page", "handleApartment");
        return "redirect:/apartment/handle?year=" + year;
    }

    @GetMapping("/register")
    public String register(Model model, Authentication authentication) {
        CustomUserDetails userDetails = currentUser(authentication);

//        // Check if user has ROLE_LANDLORD or ROLE_ADMIN
//        // Check if user has ROLE_LANDLORD or ROLE_ADMIN using the UserRole enum
//        boolean hasRoleUserOrAdmin = userDetails.getAuthorities().stream()
//            .anyMatch(authority ->
//                authority.getAuthority().equals(UserRole.ROLE_LANDLORD.toString()) ||
//                    authority.getAuthority().equals(UserRole.ROLE_ADMIN.toString())
//            );
//        // Only set ownerName if the user has either ROLE_LANDLORD or ROLE_ADMIN
//        if (hasRoleUserOrAdmin) {
//            model.addAttribute("ownerName", userDetails.getName());
//        }

        model.addAttribute("ownerName", userDetails.getName());
        model.addAttribute("page", "registerApartment");
        return "registerApartment";
    }

    @GetMapping("/handle")
    public String handle(
        @RequestParam(required = false) Integer year,
        Model model,
        Authentication authentication) {

        CustomUserDetails userDetails = currentUser(authentication);
        UUID userId = userDetails.getId();
        List<ApartmentDto> apartmentsForUser = apartmentService.getApartmentsForUser(userId);
        List<Integer> availableYears = getAvailableYears(apartmentsForUser);

        Integer latestYear = availableYears.isEmpty() ? 0 : availableYears.get(availableYears.size() - 1);

        if (year == null) {
            year = latestYear;
        }
        List<ApartmentDto> apartments = getApartmentsFiltered(apartmentsForUser, year);

        model.addAttribute("apartments", apartments);
        model.addAttribute("years", availableYears);
        model.addAttribute("selectedYear", year);
        model.addAttribute("page", "handleApartment");
        return "handleApartment";
    }

    private List<ApartmentDto> getApartmentsFiltered(List<ApartmentDto> apartmentsForUser, Integer year) {
        return apartmentsForUser.stream()
            .filter(apartment -> year == null || year.equals(apartment.getCreatedDate().getYear()))
            // Sort by year in ascending order
            .sorted(Comparator.comparing(apartment -> apartment.getCreatedDate().getYear()))
            .collect(Collectors.toList());
    }

    private List<Integer> getAvailableYears(List<ApartmentDto> apartmentsForUser) {
        return apartmentsForUser.stream()
            .map(apartment -> apartment.getCreatedDate().getYear()) // Extract the year from date
            .distinct() // Get unique years
            .sorted() // Sort the years in ascending order
            .collect(Collectors.toList()); // Collect into a list
    }

    private CustomUserDetails currentUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            return customUserDetails;
        }
        throw new IllegalStateException("Unexpected principal type");
    }
}

