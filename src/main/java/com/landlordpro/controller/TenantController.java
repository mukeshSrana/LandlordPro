package com.landlordpro.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.landlordpro.dto.TenantDto;
import com.landlordpro.dto.UserDto;
import com.landlordpro.security.CustomUserDetails;
import com.landlordpro.service.ApartmentService;
import com.landlordpro.service.TenantService;
import com.landlordpro.service.document.TenantGdprGeneratorService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/tenant")
@PreAuthorize("hasRole('ROLE_LANDLORD')")
public class TenantController {

    private final ApartmentService apartmentService;
    private final TenantService tenantService;
    private final TenantGdprGeneratorService tenantGdprGeneratorService;

    public TenantController(ApartmentService apartmentService, TenantService tenantService, TenantGdprGeneratorService tenantGdprGeneratorService) {
        this.apartmentService = apartmentService;
        this.tenantService = tenantService;
        this.tenantGdprGeneratorService = tenantGdprGeneratorService;
    }

    @PostMapping("/add")
    public String add(
        @Valid @ModelAttribute("tenant") TenantDto tenantDto,
        BindingResult bindingResult,
        Authentication authentication,
        RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                redirectAttributes.addFlashAttribute("bindingResult", bindingResult);
                redirectAttributes.addFlashAttribute("tenant", tenantDto);
                return "redirect:/tenant/register";
            }

            CustomUserDetails userDetails = currentUser(authentication);
            UUID userId = userDetails.getId();
            tenantDto.setUserId(userId);
            tenantDto.setPrivatePolicy(privatePolicy(tenantDto.getFullName(), userId));
            tenantService.add(tenantDto);
            redirectAttributes.addFlashAttribute("successMessage",
                "Tenant created successfully. "
                    + "The GDPR document has been saved under /Tenant/Handle/. "
                    + "Please send it to your tenant."
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            log.error(e.getMessage(), e);
            tenantDto.setPrivatePolicy(null);
            tenantDto.setReceiptData(null);
            redirectAttributes.addFlashAttribute("tenant", tenantDto);
        }
        redirectAttributes.addFlashAttribute("page", "registerTenant");
        return "redirect:/tenant/register";
    }

    private byte[] privatePolicy(String tenantName, UUID userId) {
        UserDto user = tenantService.getUser(userId);
        return tenantGdprGeneratorService.generateGdprPdf(tenantName, user.getName(), user.getUsername(), user.getMobileNumber());
    }

    @GetMapping("/register")
    public String register(
        @ModelAttribute("tenant") TenantDto tenantDto,
        @ModelAttribute("bindingResult") BindingResult bindingResult,
        Model model,
        Authentication authentication) {
        CustomUserDetails userDetails = currentUser(authentication);
        Map<UUID, String> apartmentIdNameMap = apartmentService.getApartmentIdNameMap(userDetails.getId());
        model.addAttribute("apartmentIdNameMap", apartmentIdNameMap);
        model.addAttribute("selectedApartment", apartmentIdNameMap.get(tenantDto.getApartmentId()));

        if (tenantDto == null) {
            tenantDto = new TenantDto();
        }
        model.addAttribute("tenant", tenantDto);
        if (bindingResult.getObjectName() != null && bindingResult != null && bindingResult.hasErrors()) {
            model.addAttribute("org.springframework.validation.BindingResult.tenant", bindingResult);
            model.addAttribute("org.springframework.validation.BindingResult.bindingResult", bindingResult);
        }
        model.addAttribute("page", "registerTenant");
        return "registerTenant";
    }

    @GetMapping("/handle")
    public String handle(
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) UUID apartmentId,
        Authentication authentication,
        Model model) {
        try {
            CustomUserDetails userDetails = currentUser(authentication);
            UUID userId = userDetails.getId();
            List<TenantDto> tenantsForUser = tenantService.getTenantsForUser(userId);

            List<Integer> availableYears = getAvailableYears(tenantsForUser);

            Integer latestYear = availableYears.isEmpty() ? 0 : availableYears.get(availableYears.size() - 1);

            if (year == null) {
                year = latestYear;
            }

            Map<UUID, String> availableApartments = getAvailableApartments(tenantsForUser, year);

            // Set apartmentId to the first available one if it's null or not present in the map
            if (apartmentId == null || !availableApartments.containsKey(apartmentId)) {
                apartmentId = availableApartments.keySet().stream().findFirst().orElse(null);
            }

            List<TenantDto> tenants = getTenantsFiltered(tenantsForUser, year, apartmentId);

            model.addAttribute("tenants", tenants);
            model.addAttribute("years", availableYears);
            model.addAttribute("apartments", availableApartments);
            model.addAttribute("selectedYear", year);
            model.addAttribute("selectedApartment", availableApartments.get(apartmentId));
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Unexpected error occurred: " + e.getMessage());
            log.error("Unexpected error while handling tenant: ", e);
        }

        model.addAttribute("page", "handleTenant");
        return "handleTenant";
    }

    private CustomUserDetails currentUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            return customUserDetails;
        }
        throw new IllegalStateException("Unexpected principal type");
    }

    private List<TenantDto> getTenantsFiltered(List<TenantDto> tenantsForUser, Integer year, UUID apartmentId) {
        return tenantsForUser.stream()
            .filter(tenant -> year == null || year.equals(tenant.getLeaseStartDate().getYear()))
            // Filter by apartmentId only if it is not null
            .filter(tenant -> apartmentId == null || apartmentId.equals(tenant.getApartmentId()))
            // Sort by year in ascending order
            .sorted(Comparator.comparing(tenant -> tenant.getLeaseStartDate().getYear()))
            .collect(Collectors.toList());
    }

    private List<Integer> getAvailableYears(List<TenantDto> tenantsForUser) {
        return tenantsForUser.stream()
            .map(tenant -> tenant.getLeaseStartDate().getYear()) // Extract the year from date
            .distinct() // Get unique years
            .sorted() // Sort the years in ascending order
            .collect(Collectors.toList()); // Collect into a list
    }

    private Map<UUID, String> getAvailableApartments(List<TenantDto> tenantsForUser, Integer year) {
        List<UUID> apartmentsIds = tenantsForUser.stream()
            .filter(tenantDto -> tenantDto.getLeaseStartDate().getYear() == year)
            .map(TenantDto::getApartmentId) // Extract apartmentId
            .distinct() // Get unique apartmentIds
            .sorted() // Sort the apartmentIds in ascending order
            .collect(Collectors.toList());
        return apartmentService.getApartmentIdNameMap(apartmentsIds);
    }
}

