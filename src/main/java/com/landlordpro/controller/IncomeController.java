package com.landlordpro.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.landlordpro.dto.IncomeDto;
import com.landlordpro.dto.enums.IncomeStatus;
import com.landlordpro.security.CustomUserDetails;
import com.landlordpro.service.ApartmentService;
import com.landlordpro.service.IncomeService;
import com.landlordpro.service.TenantService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/income")
public class IncomeController {

    private final TenantService tenantService;
    private final IncomeService incomeService;
    private final ApartmentService apartmentService;

    public IncomeController(TenantService tenantService, IncomeService incomeService, ApartmentService apartmentService) {
        this.tenantService = tenantService;
        this.incomeService = incomeService;
        this.apartmentService = apartmentService;
    }

    @PostMapping("/add")
    public String add(
        @RequestParam("apartmentId") UUID apartmentId,
        @RequestParam("tenantId") UUID tenantId,
        @RequestParam("status") String status,
        @RequestParam("amount") BigDecimal amount,
        @RequestParam("comments") String comments,
        @RequestParam("date") String date,
        @RequestParam("receiptData") MultipartFile receiptData,
        Authentication authentication,
        RedirectAttributes redirectAttributes){
        try {
            // Convert the MultipartFile to a byte[] and set it in the DTO
            byte[] receiptBytes = null;
            if (!receiptData.isEmpty()) {
                receiptBytes = receiptData.getBytes();  // Convert to byte array
            }

            // Create an ExpenseDto manually since the @ModelAttribute doesn't handle MultipartFile well
            IncomeDto incomeDto = new IncomeDto();
            incomeDto.setApartmentId(apartmentId);
            incomeDto.setStatus(status);
            incomeDto.setComments(comments);
            incomeDto.setAmount(amount);
            incomeDto.setTenantId(tenantId);
            incomeDto.setDate(LocalDate.parse(date));
            incomeDto.setReceiptData(receiptBytes);

            CustomUserDetails userDetails = currentUser(authentication);
            // Retrieve the logged-in user's ID
            UUID userId = userDetails.getId();

            incomeDto.setUserId(userId);
            incomeDto.setApartmentId(incomeDto.getApartmentId());

            incomeService.add(incomeDto);

            redirectAttributes.addFlashAttribute("successMessage", "Income created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error occurred: " + e.getMessage());
            log.error("Unexpected error while saving income: ", e);
        }
        redirectAttributes.addFlashAttribute("page", "registerIncome");
        return "redirect:/income/register";
    }

    @GetMapping("/register")
    public String register(Model model, Authentication authentication) {
        CustomUserDetails userDetails = currentUser(authentication);
        Map<UUID, String> apartmentIdNameMap = apartmentService.getApartmentIdNameMap(userDetails.getId());
        model.addAttribute("apartmentIdNameMap", apartmentIdNameMap);
        model.addAttribute("status", IncomeStatus.values());
        model.addAttribute("page", "registerIncome");
        return "registerIncome";
    }

    @GetMapping("/tenants")
    @ResponseBody
    public List<Map<String, String>> getTenants(@RequestParam("apartmentId") UUID apartmentId, Authentication authentication) {
        CustomUserDetails userDetails = currentUser(authentication);
        return tenantService.getTenantsByApartmentId(userDetails.getId(), apartmentId);
    }


    private CustomUserDetails currentUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            return customUserDetails;
        }
        throw new IllegalStateException("Unexpected principal type");
    }
}
