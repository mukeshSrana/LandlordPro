package com.landlordpro.controller;

import com.landlordpro.model.Expense;
import com.landlordpro.service.ExpenseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/list")
    public String listExpenses(Model model) throws IOException {
        List<Expense> expenses = expenseService.getAllExpenses();
        model.addAttribute("expenses", expenses);
        return "list-expense";
    }

    @GetMapping("/new")
    public String newExpenseForm(Model model) {
        model.addAttribute("expense", new Expense());
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
}
