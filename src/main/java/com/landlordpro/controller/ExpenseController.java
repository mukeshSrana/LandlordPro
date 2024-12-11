package com.landlordpro.controller;

import com.landlordpro.config.AppConfig;
import com.landlordpro.model.Expense;
import com.landlordpro.service.ExpenseService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

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
        List<Expense> expenses = expenseService.getAllExpenses();
        model.addAttribute("expenses", expenses);
        model.addAttribute("years", expenses.stream().map(Expense::getYear).distinct().collect(toList()));
        model.addAttribute("apartments", expenses.stream().map(Expense::getApartmentName).distinct().collect(toList()));
        return "expenses/list-expense";
    }

    @GetMapping("/new")
    public String newExpenseForm(Model model) {
        model.addAttribute("expense", new Expense());
        model.addAttribute("apartmentNames", appConfig.getApartmentNames()); // Add apartment names
        return "expenses/create-expense";
    }

//    @PostMapping("/create")
//    public String createExpense(@ModelAttribute Expense expense) throws IOException {
//        expenseService.saveExpense(expense);
//        return "redirect:/expenses/list";
//    }

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

    @GetMapping("/edit/{id}")
    public String editExpenseForm(@PathVariable String id, Model model) throws IOException {
        Expense expense = expenseService.getExpenseById(id).orElseThrow();
        model.addAttribute("expense", expense);
        return "expenses/update-expense";
    }

    @PostMapping("/update/{id}")
    public String updateExpense(@PathVariable String id, @ModelAttribute Expense expense) throws IOException {
        expenseService.updateExpense(id, expense);
        return "redirect:/expenses/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable String id) throws IOException {
        expenseService.deleteExpense(id);
        return "redirect:/expenses/list";
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<Void> deleteExpense(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        String year = request.get("year");
        String apartment = request.get("apartment");

        boolean isDeleted = expenseService.deleteExpense(id, year, apartment);

        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
