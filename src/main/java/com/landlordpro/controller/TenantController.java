package com.landlordpro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.landlordpro.service.ApartmentService;

@Controller
public class TenantController {

    private final ApartmentService apartmentService;

    public TenantController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @GetMapping("/registerTenant")
    public String registerIncome(Model model) {
        model.addAttribute("apartmentNames", apartmentService.apartmentNames());
        model.addAttribute("page", "registerTenant");
        return "registerTenant";
    }

    @GetMapping("/handleTenant")
    public String handleIncome(Model model) {
        model.addAttribute("page", "handleTenant");
        return "handleTenant";
    }
}

