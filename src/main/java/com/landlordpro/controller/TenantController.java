package com.landlordpro.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

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
import com.landlordpro.security.CustomUserDetails;
import com.landlordpro.service.ApartmentService;
import com.landlordpro.service.TenantService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/tenant")
public class TenantController {

    private final ApartmentService apartmentService;
    private final TenantService tenantService;

    public TenantController(ApartmentService apartmentService, TenantService tenantService) {
        this.apartmentService = apartmentService;
        this.tenantService = tenantService;
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

            // Call the service layer to save the tenant
            tenantService.add(tenantDto);

            // Add a success message
            redirectAttributes.addFlashAttribute("successMessage", "Tenant added successfully!");
        } catch (Exception e) {
            // Handle exceptions and add an error message
            redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error occurred: " + e.getMessage());
            log.error("Error adding tenant: ", e);
        }

        // Redirect to the tenant registration page
        redirectAttributes.addFlashAttribute("page", "registerTenant");
        return "redirect:/tenant/register";
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
    public String handleIncome(Model model) {
        model.addAttribute("page", "handleTenant");
        return "handleTenant";
    }

    private CustomUserDetails currentUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            return customUserDetails;
        }
        throw new IllegalStateException("Unexpected principal type");
    }
}

