package com.landlordpro.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.landlordpro.dto.ApartmentDto;
import com.landlordpro.security.CustomUserDetails;
import com.landlordpro.service.ApartmentService;

import jakarta.validation.Valid;
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
    public String add(
        @Valid @ModelAttribute("apartment") ApartmentDto apartmentDto,
        BindingResult bindingResult,
        Model model,
        Authentication authentication,
        RedirectAttributes redirectAttributes){
        try {
            if (bindingResult.hasErrors()) {
                model.addAttribute("apartment", apartmentDto);
                model.addAttribute("page", "registerApartment");
                return "registerApartment";
            }

            CustomUserDetails userDetails = currentUser(authentication);
            UUID userId = userDetails.getId();
            apartmentDto.setUserId(userId);
            apartmentService.add(apartmentDto);
            redirectAttributes.addFlashAttribute("successMessage", "Apartment created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            log.error(e.getMessage(), e);
        }
        redirectAttributes.addFlashAttribute("page", "registerApartment");
        return "redirect:/apartment/register";
    }

    @PostMapping("/update")
    public String update(
        @Valid ApartmentDto apartmentDto,
        BindingResult bindingResult,
        Authentication authentication,
        RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                Optional<ObjectError> firstError = bindingResult.getAllErrors().stream().findFirst();
                if (firstError.isPresent()) {
                    throw new RuntimeException(firstError.get().getDefaultMessage());
                }
            }

            CustomUserDetails userDetails = currentUser(authentication);
            UUID userId = userDetails.getId();

            apartmentService.update(apartmentDto, userId);
            redirectAttributes.addFlashAttribute("errorMessage", null);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            log.error(e.getMessage(), e);
        }
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
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            log.error(e.getMessage(), e);
        }
        redirectAttributes.addFlashAttribute("page", "handleApartment");
        return "redirect:/apartment/handle?year=" + year;
    }

    @GetMapping("/register")
    public String showRegisterApartment(Model model, Authentication authentication) {
        CustomUserDetails userDetails = currentUser(authentication);
        ApartmentDto apartmentDto = new ApartmentDto();
        apartmentDto.setOwnerName(userDetails.getName());
        apartmentDto.setCountry("Norway");
        model.addAttribute("apartment", apartmentDto);
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
        String ownerName = getOwnerName(apartmentsForUser);

        Integer latestYear = availableYears.isEmpty() ? 0 : availableYears.get(availableYears.size() - 1);

        if (year == null) {
            year = latestYear;
        }
        List<ApartmentDto> apartments = getApartmentsFiltered(apartmentsForUser, year);

        model.addAttribute("apartments", apartments);
        model.addAttribute("ownerName", ownerName);
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

    private String getOwnerName(List<ApartmentDto> apartmentsForUser) {
        Set<String> uniqueOwners = apartmentsForUser.stream()
            .map(ApartmentDto::getOwnerName)
            .collect(Collectors.toSet());

        if (uniqueOwners.size() > 1) {
            throw new IllegalStateException("Owner name inconsistent, can't me more than 1");
        }

        if (!uniqueOwners.isEmpty()) {
            return uniqueOwners.iterator().next();
        }

        return Strings.EMPTY;
    }

    private CustomUserDetails currentUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            return customUserDetails;
        }
        throw new IllegalStateException("Unexpected principal type");
    }
}

