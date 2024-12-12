package com.landlordpro.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.landlordpro.config.AppConfig;
import com.landlordpro.model.Expense;
import com.landlordpro.service.ExpenseService;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    private final AppConfig appConfig;
    private final ExpenseService expenseService;

    public ExpenseController(AppConfig appConfig, ExpenseService expenseService) {
        this.appConfig = appConfig;
        this.expenseService = expenseService;
    }

    @GetMapping("/list")
    public String listExpenses(Model model) throws IOException {
        // Get all expenses and sort them by year
        List<Expense> expenses = expenseService.getAllExpenses().stream()
            .sorted(Comparator.comparing(Expense::getYear)) // Sort by year ascending
            .collect(Collectors.toList());

        // Extract distinct years and apartments in one pass using streams
        List<Integer> years = expenses.stream()
            .map(Expense::getYear)
            .distinct()
            .sorted()
            .collect(Collectors.toList());

        List<String> apartments = expenses.stream()
            .map(Expense::getApartmentName)
            .distinct()
            .collect(Collectors.toList());

        // Get the latest year (last year in the sorted list)
        int latestYear = years.isEmpty() ? 0 : years.get(years.size() - 1);

        // Add attributes to the model
        model.addAttribute("expenses", expenses);
        model.addAttribute("years", years);
        model.addAttribute("apartments", apartments);
        model.addAttribute("latestYear", latestYear);

        return "expenses/list-expense";
    }


    @GetMapping("/new")
    public String newExpenseForm(Model model) {
        model.addAttribute("expense", new Expense());
        model.addAttribute("apartmentNames", appConfig.getApartmentNames()); // Add apartment names
        return "expenses/create-expense";
    }

    @GetMapping("/create")
    public String showCreateExpensePage(Model model) {
        model.addAttribute("expense", new Expense());
        model.addAttribute("activeView", "create-expense");
        return "index";
    }

    @PostMapping("/create")
    public String createExpense(@ModelAttribute Expense expense, Model model) {
        try {
            expenseService.saveExpense(expense);
            model.addAttribute("successMessage", "Expense created successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error creating expense: " + e.getMessage());
        }
        // Reset the form fields by sending a new Expense object
        model.addAttribute("expense", new Expense());
        model.addAttribute("activeView", "create-expense");
        return "index";
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

        return "redirect:/expenses/list"; // Redirect to the updated expense list
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateExpense(@RequestParam("id") String id,
        @RequestParam("year") int year,
        @RequestParam("apartmentName") String apartmentName,
        @RequestParam("name") String name,
        @RequestParam("amount") BigDecimal amount) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Logic to update the expense
            boolean isUpdated = expenseService.updateExpense(id, year, apartmentName, name, amount);

            if (isUpdated) {
                response.put("success", true);
            } else {
                response.put("success", false);
                response.put("message", "Updation failed");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

}
