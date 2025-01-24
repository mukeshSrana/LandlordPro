package com.landlordpro.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.landlordpro.dto.IncomeDto;
import com.landlordpro.dto.enums.IncomeStatus;
import com.landlordpro.security.CustomUserDetails;
import com.landlordpro.service.ApartmentService;
import com.landlordpro.service.IncomeService;
import com.landlordpro.service.TenantService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/income")
public class IncomeController {

    private final TenantService tenantService;
    private final IncomeService incomeService;
    private final ApartmentService apartmentService;

    public IncomeController(TenantService tenantService, IncomeService incomeService, ApartmentService apartmentService) {
        this.tenantService = tenantService;
        this.incomeService = incomeService;
        this.apartmentService = apartmentService;
    }

    @PostMapping("/add")
    public String add(
        @RequestParam("apartmentId") UUID apartmentId,
        @RequestParam("tenantId") UUID tenantId,
        @RequestParam("status") String status,
        @RequestParam("amount") BigDecimal amount,
        @RequestParam("comments") String comments,
        @RequestParam("date") String date,
        @RequestParam("receiptData") MultipartFile receiptData,
        Authentication authentication,
        RedirectAttributes redirectAttributes){
        try {
            // Convert the MultipartFile to a byte[] and set it in the DTO
            byte[] receiptBytes = null;
            if (!receiptData.isEmpty()) {
                receiptBytes = receiptData.getBytes();  // Convert to byte array
            }

            IncomeDto incomeDto = new IncomeDto();
            incomeDto.setApartmentId(apartmentId);
            incomeDto.setStatus(status);
            incomeDto.setComments(comments);
            incomeDto.setAmount(amount);
            incomeDto.setTenantId(tenantId);
            incomeDto.setDate(LocalDate.parse(date));
            incomeDto.setReceiptData(receiptBytes);

            CustomUserDetails userDetails = currentUser(authentication);
            // Retrieve the logged-in user's ID
            UUID userId = userDetails.getId();

            incomeDto.setUserId(userId);
            incomeDto.setApartmentId(incomeDto.getApartmentId());

            incomeService.add(incomeDto);

            redirectAttributes.addFlashAttribute("successMessage", "Income created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error occurred: " + e.getMessage());
            log.error("Unexpected error while saving income: ", e);
        }
        redirectAttributes.addFlashAttribute("page", "registerIncome");
        return "redirect:/income/register";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") UUID id,
        @RequestParam("userId") UUID userId,
        @RequestParam("apartmentId") UUID apartmentId,
        Authentication authentication,
        RedirectAttributes redirectAttributes) {
        try {
            CustomUserDetails userDetails = currentUser(authentication);
            // Retrieve the logged-in user's ID
            if (userDetails.getId().equals(userId)) {
                incomeService.deleteIncome(id, userId, apartmentId);
            } else {
                throw new RuntimeException("Logged in userId is not same as the deleted income userId");
            }
            redirectAttributes.addFlashAttribute("successMessage", "Income deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error occurred: " + e.getMessage());
            log.error("Unexpected error while deleting income: ", e);
        }
        redirectAttributes.addFlashAttribute("page", "handleIncome");
        return "redirect:/income/handle";
    }

    @GetMapping("/register")
    public String register(Model model, Authentication authentication) {
        CustomUserDetails userDetails = currentUser(authentication);
        Map<UUID, String> apartmentIdNameMap = apartmentService.getApartmentIdNameMap(userDetails.getId());
        model.addAttribute("apartmentIdNameMap", apartmentIdNameMap);
        model.addAttribute("status", IncomeStatus.values());
        model.addAttribute("page", "registerIncome");
        return "registerIncome";
    }

    @GetMapping("/tenants")
    @ResponseBody
    public List<Map<String, String>> getActiveTenants(@RequestParam("apartmentId") UUID apartmentId, Authentication authentication) {
        CustomUserDetails userDetails = currentUser(authentication);
        return tenantService.findActiveTenantsByUserIdAndApartmentId(userDetails.getId(), apartmentId);
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
            List<IncomeDto> incomesForUser = incomeService.getIncomeForUser(userId);

            List<Integer> availableYears = getAvailableYears(incomesForUser);
            Map<UUID, String> availableApartments = getAvailableApartments(incomesForUser);
            Map<UUID, String> availableTenants = getAvailableTenants(incomesForUser);

            List<IncomeDto> incomes = getIncomeFiltered(incomesForUser, year, apartmentId);

            model.addAttribute("incomes", incomes);
            model.addAttribute("years", availableYears);
            model.addAttribute("apartments", availableApartments);
            model.addAttribute("tenants", availableTenants);
            model.addAttribute("selectedYear", year);
            model.addAttribute("selectedApartment", availableApartments.get(apartmentId));
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Unexpected error occurred: " + e.getMessage());
            log.error("Unexpected error while handling income: ", e);
        }
        model.addAttribute("page", "handleIncome");
        return "handleIncome";
    }

    @GetMapping("/downloadReceipt/{id}")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable UUID id, Model model) {
        try {
            IncomeDto incomeDto = incomeService.findById(id);

            // Check if receiptData is null
            if (incomeDto.getReceiptData() == null) {
                // Return a 404 Not Found or 204 No Content if the receipt is missing
                return ResponseEntity.notFound().build(); // or ResponseEntity.noContent().build();
            }

            // Retrieve the receipt data (byte array)
            byte[] receiptData = incomeDto.getReceiptData();

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
            model.addAttribute("errorMessage", "Unexpected error occurred: " + e.getMessage());
            log.error("Unexpected error while downloading income receipt: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private List<Integer> getAvailableYears(List<IncomeDto> incomesForUser) {
        return incomesForUser.stream()
            .map(income -> income.getDate().getYear()) // Extract the year from date
            .distinct() // Get unique years
            .sorted() // Sort the years in ascending order
            .collect(Collectors.toList()); // Collect into a list
    }

    private Map<UUID, String> getAvailableApartments(List<IncomeDto> incomesForUser) {
        List<UUID> apartmentsIds = incomesForUser.stream()
            .map(IncomeDto::getApartmentId) // Extract apartmentId
            .distinct() // Get unique apartmentIds
            .sorted() // Sort the apartmentIds in ascending order
            .collect(Collectors.toList());
        return apartmentService.getApartmentIdNameMap(apartmentsIds);
    }

    private Map<UUID, String> getAvailableTenants(List<IncomeDto> incomesForUser) {
        List<UUID> tenantsIds = incomesForUser.stream()
            .map(IncomeDto::getTenantId) // Extract apartmentId
            .distinct() // Get unique apartmentIds
            .sorted() // Sort the apartmentIds in ascending order
            .collect(Collectors.toList());
        return tenantService.getTenantIdNameMap(tenantsIds);
    }

    private List<IncomeDto> getIncomeFiltered(List<IncomeDto> incomesForUser, Integer year, UUID apartmentId) {
        return incomesForUser.stream()
            .filter(income -> year == null || year.equals(income.getDate().getYear()))
            // Filter by apartmentId only if it is not null
            .filter(income -> apartmentId == null || apartmentId.equals(income.getApartmentId()))
            // Sort by year in ascending order
            .sorted(Comparator.comparing(income -> income.getDate().getYear()))
            .collect(Collectors.toList());
    }

    private CustomUserDetails currentUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            return customUserDetails;
        }
        throw new IllegalStateException("Unexpected principal type");
    }
}
