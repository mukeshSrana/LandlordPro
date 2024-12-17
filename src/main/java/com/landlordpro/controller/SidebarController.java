package com.landlordpro.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/rapport-1")
    public String rapport1(Model model) {
        model.addAttribute("page", "rapport-1");
        return "rapport-1";
    }

    @GetMapping("/rapport-2")
    public String rapport2(Model model) {
        model.addAttribute("page", "rapport-2");
        return "rapport-2";
    }
}

