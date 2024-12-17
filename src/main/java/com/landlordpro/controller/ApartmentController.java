package com.landlordpro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.landlordpro.config.AppConfig;
import com.landlordpro.model.Expense;
import com.landlordpro.service.ExpenseService;

@Controller
public class ApartmentController {
    private final AppConfig appConfig;
    private final ExpenseService expenseService;

    public ApartmentController(AppConfig appConfig, ExpenseService expenseService) {
        this.appConfig = appConfig;
        this.expenseService = expenseService;
    }

    @PostMapping("/saveApartment")
    public String saveExpense(@ModelAttribute Expense expense, Model model) {
        try {
            expenseService.saveExpense(expense);
            model.addAttribute("successMessage", "Expense created successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error creating expense: " + e.getMessage());
        }
        model.addAttribute("apartmentNames", appConfig.getApartmentNames());
        model.addAttribute("page", "registerExpense");
        return "redirect:/registerExpense";
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

