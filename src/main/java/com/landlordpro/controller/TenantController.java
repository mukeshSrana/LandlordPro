package com.landlordpro.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.landlordpro.dto.TenantDto;
import com.landlordpro.dto.UserDto;
import com.landlordpro.security.CustomUserDetails;
import com.landlordpro.service.ApartmentService;
import com.landlordpro.service.TenantGdprGeneratorService;
import com.landlordpro.service.TenantService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/tenant")
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
    public String addTenant(
        @RequestParam("fullName") String fullName,
        @RequestParam("dateOfBirth") String dateOfBirth,
        @RequestParam("phoneNumber") String phoneNumber,
        @RequestParam("email") String email,
        @RequestParam("receiptData") MultipartFile receiptData,
        @RequestParam("apartmentId") UUID apartmentId,
        @RequestParam("leaseStartDate") String leaseStartDate,
        @RequestParam(value = "leaseEndDate", required = false) String leaseEndDate,
        @RequestParam("monthlyRent") BigDecimal monthlyRent,
        @RequestParam(value = "securityDeposit") BigDecimal securityDeposit,
        @RequestParam(value = "securityInstitution") String securityDepositInstitutionName,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        try {
            // Convert the MultipartFile to a byte[] and set it in the DTO
            byte[] receiptBytes = null;
            if (!receiptData.isEmpty()) {
                receiptBytes = receiptData.getBytes();  // Convert to byte array
            }
            // Parse dates from strings
            LocalDate dob = LocalDate.parse(dateOfBirth);
            LocalDate leaseStart = LocalDate.parse(leaseStartDate);
            LocalDate leaseEnd = (leaseEndDate != null && !leaseEndDate.isEmpty())
                ? LocalDate.parse(leaseEndDate)
                : null;
            // Get the logged-in user's details
            CustomUserDetails userDetails = currentUser(authentication);
            UUID userId = userDetails.getId();

            // Create a TenantDto
            TenantDto tenantDto = new TenantDto();
            tenantDto.setFullName(fullName);
            tenantDto.setDateOfBirth(dob);
            tenantDto.setPhoneNumber(phoneNumber);
            tenantDto.setEmail(email);
            tenantDto.setReceiptData(receiptBytes);
            tenantDto.setApartmentId(apartmentId);
            tenantDto.setLeaseStartDate(leaseStart);
            tenantDto.setLeaseEndDate(leaseEnd);
            tenantDto.setMonthlyRent(monthlyRent);
            tenantDto.setSecurityDeposit(securityDeposit);
            tenantDto.setSecurityDepositInstitutionName(securityDepositInstitutionName);
            tenantDto.setUserId(userId);

            tenantDto.setPrivatePolicy(privatePolicy(tenantDto.getFullName(), userId));

            // Call the service layer to save the tenant
            tenantService.add(tenantDto);

            // Add a success message
            redirectAttributes.addFlashAttribute("successMessage",
                "Tenant added successfully. "
                    + "The GDPR document has been saved under /Tenant/Handle/. "
                    + "Please send it to your tenant."
            );
        } catch (Exception e) {
            // Handle exceptions and add an error message
            redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error occurred: " + e.getMessage());
            log.error("Error adding tenant: ", e);
        }

        // Redirect to the tenant registration page
        redirectAttributes.addFlashAttribute("page", "registerTenant");
        return "redirect:/tenant/register";
    }

    private byte[] privatePolicy(String tenantName, UUID userId) {
        UserDto user = tenantService.getUser(userId);
        return tenantGdprGeneratorService.generateGdprPdf(tenantName, user.getName(), user.getUsername(), user.getMobileNumber());
    }

    @GetMapping("/register")
    public String register(Model model, Authentication authentication) {
        CustomUserDetails userDetails = currentUser(authentication);
        Map<UUID, String> apartmentIdNameMap = apartmentService.getApartmentIdNameMap(userDetails.getId());
        model.addAttribute("apartmentIdNameMap", apartmentIdNameMap);
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

