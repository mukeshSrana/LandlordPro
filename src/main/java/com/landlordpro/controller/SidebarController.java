package com.landlordpro.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.landlordpro.model.Expense;
import com.landlordpro.service.ApartmentService;
import com.landlordpro.service.ExpenseService;

@Controller
public class SidebarController {
    private final ExpenseService expenseService;
    private final ApartmentService apartmentService;

    public SidebarController(ExpenseService expenseService, ApartmentService apartmentService) {
        this.expenseService = expenseService;
        this.apartmentService = apartmentService;
    }

    @GetMapping("/registerExpense")
    public String registerExpense(Model model) {
        model.addAttribute("page", "registerExpense");
        //model.addAttribute("apartmentNames", apartmentService.apartmentNames());
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
        //model.addAttribute("apartmentNames", apartmentService.apartmentNames());
        model.addAttribute("page", "registerExpense");
        return "redirect:/registerExpense";
    }

    @PostMapping("/delete")
    public String deleteExpense(@RequestParam("id") String id,
        @RequestParam("year") String year,
        @RequestParam("apartmentName") String apartment,
        RedirectAttributes redirectAttributes) {
        // Logic to delete the expense using the provided details
        boolean isDeleted = expenseService.deleteExpense(id, year, apartment);

        if (isDeleted) {
            redirectAttributes.addFlashAttribute("message", "Expense deleted successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } else {
            redirectAttributes.addFlashAttribute("message", "Expense could not be found or deleted.");
            redirectAttributes.addFlashAttribute("messageType", "error");
        }

        // Include year and apartmentName as query parameters in the redirect URL
        return "redirect:/handleExpense?year=" + year + "&apartmentName=" + apartment;
    }

    @PostMapping("/update")
    public String updateExpense(@RequestParam("id") String id,
        @RequestParam("year") String year,
        @RequestParam("apartmentName") String apartmentName,
        @RequestParam("name") String name,
        @RequestParam("amount") BigDecimal amount) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Logic to update the expense
            boolean isUpdate = expenseService.updateExpense(id, year, apartmentName, name, amount);

            if (isUpdate) {
                response.put("success", true);
                response.put("updatedExpense", isUpdate);
            } else {
                response.put("success", false);
                response.put("message", "Update failed: Expense not found or could not be updated.");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }

        return "redirect:/handleExpense?year=" + year + "&apartmentName=" + apartmentName;
    }

    @GetMapping("/registerIncome")
    public String registerIncome(Model model) {
        model.addAttribute("page", "registerIncome");
        return "registerIncome";
    }

    @GetMapping("/handleIncome")
    public String handleIncome(Model model) {
        model.addAttribute("page", "handleIncome");
        return "handleIncome";
    }

    @GetMapping("/monthlyIncomeReport")
    public String monthlyIncomeReport(Model model) {
        model.addAttribute("page", "monthlyIncomeReport");
        return "monthlyIncomeReport";
    }

    @GetMapping("/incomeExpenseSummaryReport")
    public String incomeExpenseSummaryReport(Model model) {
        model.addAttribute("page", "incomeExpenseSummaryReport");
        return "incomeExpenseSummaryReport";
    }
}

