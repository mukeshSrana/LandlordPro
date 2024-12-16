package com.landlordpro.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public String listExpenses(
        @RequestParam(required = false) String year,
        @RequestParam(required = false) String apartmentName,
        Model model) throws IOException {

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
        //model.addAttribute("expense", new Expense());
        //model.addAttribute("activeView", "create-expense");
        model.addAttribute("page", "registerExpense");
        return "registerExpense";
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
        @RequestParam("year") String year,
        @RequestParam("apartmentName") String apartmentName,
        @RequestParam("name") String name,
        @RequestParam("amount") BigDecimal amount) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Logic to update the expense
            Expense updatedExpense = expenseService.updateExpense(id, year, apartmentName, name, amount);

            if (updatedExpense != null) {
                response.put("success", true);
                response.put("updatedExpense", updatedExpense);
            } else {
                response.put("success", false);
                response.put("message", "Update failed: Expense not found or could not be updated.");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}
