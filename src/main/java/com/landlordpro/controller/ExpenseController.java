package com.landlordpro.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.landlordpro.config.AppConfig;
import com.landlordpro.model.Expense;
import com.landlordpro.service.ExpenseService;

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

    @PostMapping("/modify")
    public String modifyExpense(@RequestParam("id") String id,
        @RequestParam("year") int year,
        @RequestParam("apartmentName") String apartmentName,
        @RequestParam("name") String name,
        @RequestParam("amount") BigDecimal amount) {
        // Logic to update the expense
        boolean isUpdated = expenseService.updateExpense(id, year, apartmentName, name, amount);

        if (isUpdated) {
            return "redirect:/expenses/list?message=Expense updated successfully&type=success";
        } else {
            return "redirect:/expenses?message=Failed to update expense&type=error";
        }
    }
}
