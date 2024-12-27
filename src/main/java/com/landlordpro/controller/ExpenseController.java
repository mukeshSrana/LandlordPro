package com.landlordpro.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.tika.Tika;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.landlordpro.config.AppConfig;
import com.landlordpro.dto.DeductibleExpense;
import com.landlordpro.dto.ExpenseDto;
import com.landlordpro.model.Expense;
import com.landlordpro.security.CustomUserDetails;
import com.landlordpro.service.ApartmentService;
import com.landlordpro.service.ExpenseService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/expense")
public class ExpenseController {

    private final AppConfig appConfig;
    private final ExpenseService expenseService;
    private final ApartmentService apartmentService;

    public ExpenseController(AppConfig appConfig, ExpenseService expenseService, ApartmentService apartmentService) {
        this.appConfig = appConfig;
        this.expenseService = expenseService;
        this.apartmentService = apartmentService;
    }

    @GetMapping("/downloadReceipt/{id}")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable UUID id) {
        try {
            // Fetch the Expense object using the provided ID
            ExpenseDto expenseDto = expenseService.findById(id);

            // Check if receiptData is null
            if (expenseDto.getReceiptData() == null) {
                // Return a 404 Not Found or 204 No Content if the receipt is missing
                return ResponseEntity.notFound().build(); // or ResponseEntity.noContent().build();
            }

            // Retrieve the receipt data (byte array)
            byte[] receiptData = expenseDto.getReceiptData();

            // Use Apache Tika to detect the file's MIME type
            Tika tika = new Tika();
            String mimeType = tika.detect(receiptData);

            // Set the appropriate Content-Type for the file based on its MIME type
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(mimeType));

            // Set the file's disposition as inline (display it in the browser)
            headers.setContentDisposition(ContentDisposition.inline().filename("receipt").build());

            // Return the file as a ResponseEntity
            return new ResponseEntity<>(receiptData, headers, HttpStatus.OK);
        } catch (Exception e) {
            throw e;
        }
    }


    @PostMapping("/add")
    public String add(
        @RequestParam("apartmentId") UUID apartmentId,
        @RequestParam("category") String category,
        @RequestParam("name") String name,
        @RequestParam("amount") BigDecimal amount,
        @RequestParam("expenseLocation") String expenseLocation,
        @RequestParam("date") String date,
        @RequestParam("receiptData") MultipartFile receiptData,
//        @ModelAttribute ExpenseDto expenseDto,
//        @RequestParam("receiptData") MultipartFile receiptData,
        Authentication authentication,
        RedirectAttributes redirectAttributes){
        try {
            // Convert the MultipartFile to a byte[] and set it in the DTO
            byte[] receiptBytes = null;
            if (!receiptData.isEmpty()) {
                receiptBytes = receiptData.getBytes();  // Convert to byte array
            }

            // Create an ExpenseDto manually since the @ModelAttribute doesn't handle MultipartFile well
            ExpenseDto expenseDto = new ExpenseDto();
            expenseDto.setApartmentId(apartmentId);
            expenseDto.setCategory(category);
            expenseDto.setName(name);
            expenseDto.setAmount(amount);
            expenseDto.setExpenseLocation(expenseLocation);
            expenseDto.setDate(LocalDate.parse(date));
            expenseDto.setReceiptData(receiptBytes);

            CustomUserDetails userDetails = currentUser(authentication);
            // Retrieve the logged-in user's ID
            UUID userId = userDetails.getId();

            expenseDto.setUserId(userId);
            expenseDto.setApartmentId(expenseDto.getApartmentId());

            expenseService.add(expenseDto);

            redirectAttributes.addFlashAttribute("successMessage", "Expense created successfully!");
            //return "registerApartment"; // Redirect or forward to success page
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error occurred: " + e.getMessage());
            log.error("Unexpected error while saving expense: ", e);
            //return "registerApartment"; // Return to the form with error message
        }
        redirectAttributes.addFlashAttribute("page", "registerExpense");
        return "redirect:/expense/register";
    }

    @GetMapping("/register")
    public String register(Model model, Authentication authentication) {
        CustomUserDetails userDetails = currentUser(authentication);
        Map<UUID, String> apartmentIdNameMap = apartmentService.getApartmentIdNameMap(userDetails.getId());
        model.addAttribute("apartmentIdNameMap", apartmentIdNameMap);
        model.addAttribute("categories", DeductibleExpense.values());
        model.addAttribute("page", "registerExpense");
        return "registerExpense";
    }


    @GetMapping("/handle")
    public String listExpenses(
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) UUID apartmentId,
        Authentication authentication,
        Model model) {
        try {
            CustomUserDetails userDetails = currentUser(authentication);
            UUID userId = userDetails.getId();
            List<ExpenseDto> expensesForUser = expenseService.getExpensesForUser(userId);

            List<Integer> availableYears = getAvailableYears(expensesForUser);
            Map<UUID, String> availableApartments = getAvailableApartments(expensesForUser);

//            Integer latestYear = availableYears.isEmpty() ? 0 : availableYears.get(availableYears.size() - 1);
//
//            if (year == null) {
//                year = latestYear;
//            }

            List<ExpenseDto> expenses = getExpensesFiltered(expensesForUser, year, apartmentId);

            model.addAttribute("expenses", expenses);
            model.addAttribute("years", availableYears);
            model.addAttribute("apartments", availableApartments);
            model.addAttribute("selectedYear", year);
            model.addAttribute("selectedApartment", availableApartments.get(apartmentId));
        } catch (Exception e) {
            throw e;
        }
        model.addAttribute("page", "handleExpense");
        return "handleExpense";
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

        return ResponseEntity.ok(response);
    }

    private List<Integer> getAvailableYears(List<ExpenseDto> expensesForUser) {
        return expensesForUser.stream()
            .map(expense -> expense.getDate().getYear()) // Extract the year from date
            .distinct() // Get unique years
            .sorted() // Sort the years in ascending order
            .collect(Collectors.toList()); // Collect into a list
    }

    private Map<UUID, String> getAvailableApartments(List<ExpenseDto> expensesForUser) {
        List<UUID> apartmentsIds = expensesForUser.stream()
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
            .collect(Collectors.toList());
    }

    private CustomUserDetails currentUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            return customUserDetails;
        }
        throw new IllegalStateException("Unexpected principal type");
    }
}
