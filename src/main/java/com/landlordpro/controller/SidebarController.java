package com.landlordpro.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.landlordpro.config.AppConfig;
import com.landlordpro.model.Expense;
import com.landlordpro.service.ExpenseService;

@Controller
public class SidebarController {
    private final AppConfig appConfig;
    private final ExpenseService expenseService;

    public SidebarController(AppConfig appConfig, ExpenseService expenseService) {
        this.appConfig = appConfig;
        this.expenseService = expenseService;
    }

    @GetMapping("/registerExpense")
    public String registerExpense(Model model) {
        model.addAttribute("page", "registerExpense");
        model.addAttribute("apartmentNames", appConfig.getApartmentNames());
        return "registerExpense";
    }

    @PostMapping("/saveExpense")
    public String createExpense(@ModelAttribute Expense expense, Model model) {
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

    @GetMapping("/handleExpense")
    public String handleExpense(@RequestParam(required = false) String year,
        @RequestParam(required = false) String apartmentName, Model model) throws IOException {
        List<String> availableYears = expenseService.getAvailableYears();
        List<String> availableApartments = expenseService.getAvailableApartments();

        String latestYear = availableYears.isEmpty() ? "0" : availableYears.get(availableYears.size() - 1);

        if (year == null || year.isEmpty()) {
            year = latestYear;
        }

        List<Expense> expenses = expenseService.getExpensesFiltered(year, apartmentName);

        model.addAttribute("expenses", expenses);
        model.addAttribute("years", availableYears);
        model.addAttribute("apartments", availableApartments);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedApartment", apartmentName);

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

