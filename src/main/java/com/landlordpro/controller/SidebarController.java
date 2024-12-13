package com.landlordpro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SidebarController {

    @GetMapping("/registerExpense")
    public String registerExpense(Model model) {
        model.addAttribute("page", "registerExpense");
        return "registerExpense";
    }

    @GetMapping("/handleExpense")
    public String handleExpense(Model model) {
        model.addAttribute("page", "handleExpense");
        return "handleExpense";
    }

    @GetMapping("/registerApartment")
    public String registerApartment(Model model) {
        model.addAttribute("page", "registerApartment");
        return "registerApartment";
    }

    @GetMapping("/handleApartment")
    public String handleApartment(Model model) {
        model.addAttribute("page", "handleApartment");
        return "handleApartment";
    }
}

