package com.landlordpro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TenantController {

    @GetMapping("/registerTenant")
    public String registerIncome(Model model) {
        model.addAttribute("page", "registerTenant");
        return "registerTenant";
    }

    @GetMapping("/handleTenant")
    public String handleIncome(Model model) {
        model.addAttribute("page", "handleTenant");
        return "handleTenant";
    }
}

