package com.landlordpro.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.landlordpro.config.AppConfig;
import com.landlordpro.dto.ExpenseDto;
import com.landlordpro.dto.constants.DeductibleExpense;
import com.landlordpro.security.CustomUserDetails;
import com.landlordpro.service.ApartmentService;
import com.landlordpro.service.ExpenseService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/expense")
@PreAuthorize("hasRole('ROLE_LANDLORD')")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final ApartmentService apartmentService;

    public ExpenseController(AppConfig appConfig, ExpenseService expenseService, ApartmentService apartmentService) {
        this.expenseService = expenseService;
        this.apartmentService = apartmentService;
    }

    @PostMapping("/add")
    public String add(
        @Valid @ModelAttribute("expense") ExpenseDto expenseDto,
        BindingResult bindingResult,
        Authentication authentication,
        RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                redirectAttributes.addFlashAttribute("bindingResult", bindingResult);
                redirectAttributes.addFlashAttribute("expense", expenseDto);
                return "redirect:/expense/register";
            }

            CustomUserDetails userDetails = currentUser(authentication);
            UUID userId = userDetails.getId();
            expenseDto.setUserId(userId);
            expenseService.add(expenseDto);
            redirectAttributes.addFlashAttribute("successMessage", "Expense created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            log.error(e.getMessage(), e);
        }
        redirectAttributes.addFlashAttribute("page", "registerExpense");
        return "redirect:/expense/register";
    }

    @GetMapping("/register")
    public String register(
        @ModelAttribute("expense") ExpenseDto expenseDto,
        @ModelAttribute("bindingResult") BindingResult bindingResult,
        Model model,
        Authentication authentication) {
        CustomUserDetails userDetails = currentUser(authentication);
        Map<UUID, String> apartmentIdNameMap = apartmentService.getApartmentIdNameMap(userDetails.getId());
        model.addAttribute("apartmentIdNameMap", apartmentIdNameMap);
        model.addAttribute("categories", DeductibleExpense.values());
        model.addAttribute("selectedCategory", expenseDto.getCategory());
        model.addAttribute("selectedApartment", apartmentIdNameMap.get(expenseDto.getApartmentId()));

        if (expenseDto == null) {
            expenseDto = new ExpenseDto();
        }
        model.addAttribute("expense", expenseDto);
        if (bindingResult.getObjectName() != null && bindingResult != null && bindingResult.hasErrors()) {
            model.addAttribute("org.springframework.validation.BindingResult.expense", bindingResult);
            model.addAttribute("org.springframework.validation.BindingResult.bindingResult", bindingResult);
        }
        model.addAttribute("page", "registerExpense");
        return "registerExpense";
    }

    @GetMapping("/handle")
    public String handle(
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) UUID apartmentId,
        Authentication authentication,
        Model model) {
        try {
            CustomUserDetails userDetails = currentUser(authentication);
            UUID userId = userDetails.getId();
            List<ExpenseDto> expensesForUser = expenseService.getExpensesForUser(userId);

            List<Integer> availableYears = getAvailableYears(expensesForUser);

            Integer latestYear = availableYears.isEmpty() ? 0 : availableYears.get(availableYears.size() - 1);

            if (year == null) {
                year = latestYear;
            }

            Map<UUID, String> availableApartments = getAvailableApartments(expensesForUser, year);

            // Set apartmentId to the first available one if it's null or not present in the map
            if (apartmentId == null || !availableApartments.containsKey(apartmentId)) {
                apartmentId = availableApartments.keySet().stream().findFirst().orElse(null);
            }
            List<ExpenseDto> expenses = getExpensesFiltered(expensesForUser, year, apartmentId);

            model.addAttribute("expenses", expenses);
            model.addAttribute("categories", DeductibleExpense.values());
            model.addAttribute("years", availableYears);
            model.addAttribute("apartments", availableApartments);
            model.addAttribute("selectedYear", year);
            model.addAttribute("selectedApartment", availableApartments.get(apartmentId));
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            log.error(e.getMessage(), e);
        }
        model.addAttribute("page", "handleExpense");
        return "handleExpense";
    }

    @PostMapping("/delete")
    public String deleteExpense(@RequestParam("id") UUID id,
        @RequestParam("userId") UUID userId,
        @RequestParam("apartmentId") UUID apartmentId,
        @RequestParam("year") Integer year,
        Authentication authentication,
        RedirectAttributes redirectAttributes) {
        try {
            CustomUserDetails userDetails = currentUser(authentication);
            if (userDetails.getId().equals(userId)) {
                expenseService.deleteExpense(id, userId, apartmentId);
            } else {
                throw new RuntimeException("Logged in userId is not same as the deleted expense userId");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            log.error(e.getMessage(), e);
        }
        redirectAttributes.addFlashAttribute("page", "handleExpense");
        return "redirect:/expense/handle?year=" + year + "&apartmentId=" + apartmentId;
    }

    @PostMapping("/update")
    public String update(
        @Valid ExpenseDto expenseDto,
        BindingResult bindingResult,
        Authentication authentication,
        RedirectAttributes redirectAttributes,
        HttpSession session) {
        try {
            if (bindingResult.hasErrors()) {
                Optional<ObjectError> firstError = bindingResult.getAllErrors().stream().findFirst();
                if (firstError.isPresent()) {
                    throw new RuntimeException(firstError.get().getDefaultMessage());
                }
            }

            CustomUserDetails userDetails = currentUser(authentication);
            UUID userId = userDetails.getId();

            // Check for a temporarily uploaded receipt file in the session
            byte[] tempReceiptData = (byte[]) session.getAttribute("temporaryReceipt_" + expenseDto.getId());
            if (tempReceiptData != null) {
                // Set the receipt data (file path or filename) to the expenseDto
                expenseDto.setReceiptData(tempReceiptData);
                // Clear the temporary session attributes after processing
                session.removeAttribute("temporaryReceipt_" + expenseDto.getId());
            }

            expenseDto.normalizeReceiptData();
            expenseService.update(expenseDto, userId);
            redirectAttributes.addFlashAttribute("errorMessage", null);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            log.error(e.getMessage(), e);
        }

        Integer year = expenseDto.getDate().getYear();
        UUID apartmentId = expenseDto.getApartmentId();

        redirectAttributes.addFlashAttribute("page", "handleExpense");
        return "redirect:/expense/handle?year=" + year + "&apartmentId=" + apartmentId;
    }

    private List<Integer> getAvailableYears(List<ExpenseDto> expensesForUser) {
        return expensesForUser.stream()
            .map(expense -> expense.getDate().getYear()) // Extract the year from date
            .distinct() // Get unique years
            .sorted() // Sort the years in ascending order
            .collect(Collectors.toList()); // Collect into a list
    }

    private Map<UUID, String> getAvailableApartments(List<ExpenseDto> expensesForUser, Integer year) {
        List<UUID> apartmentsIds = expensesForUser.stream()
            .filter(expenseDto -> expenseDto.getDate().getYear() == year)
            .map(ExpenseDto::getApartmentId) // Extract apartmentId
            .distinct() // Get unique apartmentIds
            .sorted() // Sort the apartmentIds in ascending order
            .collect(Collectors.toList());
        return apartmentService.getApartmentIdNameMap(apartmentsIds);
    }

    private List<ExpenseDto> getExpensesFiltered(List<ExpenseDto> expensesForUser, Integer year, UUID apartmentId) {
        return expensesForUser.stream()
            // Filter by year, comparing against the year extracted from expense's date
            .filter(expense -> year == null || year.equals(expense.getDate().getYear()))
            // Filter by apartmentId only if it is not null
            .filter(expense -> apartmentId == null || apartmentId.equals(expense.getApartmentId()))
            // Sort by year in ascending order
            .sorted(Comparator.comparing(expense -> expense.getDate().getYear()))
            .collect(Collectors.toList());
    }


    private CustomUserDetails currentUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            return customUserDetails;
        }
        throw new IllegalStateException("Unexpected principal type");
    }
}
