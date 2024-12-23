package com.landlordpro.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.landlordpro.exception.DuplicateRecordException;
import com.landlordpro.model.Tenant;
import com.landlordpro.service.ApartmentService;
import com.landlordpro.service.TenantService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class TenantController {

    private final ApartmentService apartmentService;
    private final TenantService tenantService;

    public TenantController(ApartmentService apartmentService, TenantService tenantService) {
        this.apartmentService = apartmentService;
        this.tenantService = tenantService;
    }

    @PostMapping("/saveTenant")
    public String save(@ModelAttribute Tenant tenant, Model model) {
        try {
            tenantService.save(tenant);
            model.addAttribute("successMessage", "Tenant created successfully!");
        } catch (DuplicateRecordException e) {
            model.addAttribute("errorMessage", "Error creating tenant: " + e.getMessage());
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Error creating tenant: " + e.getMessage());
            log.error("IOException while saving apartment: " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Unexpected error occurred: " + e.getMessage());
        }
        return "redirect:/registerTenant";
    }

    @GetMapping("/registerTenant")
    public String registerIncome(Model model) {
        //model.addAttribute("apartmentNamesWithId", apartmentService.apartmentNamesWithId());
        model.addAttribute("page", "registerTenant");
        return "registerTenant";
    }

    @GetMapping("/handleTenant")
    public String handleIncome(Model model) {
        model.addAttribute("page", "handleTenant");
        return "handleTenant";
    }
}

