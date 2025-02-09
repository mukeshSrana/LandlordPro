package com.landlordpro.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.tika.Tika;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.landlordpro.dto.IncomeDto;
import com.landlordpro.dto.constants.IncomeStatus;
import com.landlordpro.security.CustomUserDetails;
import com.landlordpro.service.ApartmentService;
import com.landlordpro.service.IncomeService;
import com.landlordpro.service.TenantService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/income")
@PreAuthorize("hasRole('ROLE_LANDLORD')")
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
        @Valid @ModelAttribute("income") IncomeDto incomeDto,
        BindingResult bindingResult,
        Authentication authentication,
        RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                redirectAttributes.addFlashAttribute("bindingResult", bindingResult);
                redirectAttributes.addFlashAttribute("income", incomeDto);
                return "redirect:/income/register";
            }

            CustomUserDetails userDetails = currentUser(authentication);
            UUID userId = userDetails.getId();
            incomeDto.setUserId(userId);
            incomeService.add(incomeDto);
            redirectAttributes.addFlashAttribute("successMessage", "Income created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            log.error(e.getMessage(), e);
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

    @PostMapping("/update")
    public String update(@ModelAttribute IncomeDto incomeDto, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            CustomUserDetails userDetails = currentUser(authentication);
            // Retrieve the logged-in user's ID
            UUID userId = userDetails.getId();

            if (incomeDto.getReceiptData() != null && incomeDto.getReceiptData().length == 0) {
                incomeDto.setReceiptData(null);
            }

            incomeService.update(incomeDto, userId);

            redirectAttributes.addFlashAttribute("successMessage", "Income updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error occurred: " + e.getMessage());
            log.error("Unexpected error while updating Income: ", e);
        }
        redirectAttributes.addFlashAttribute("page", "handleIncome");
        return "redirect:/income/handle";
    }

    @GetMapping("/register")
    public String register(
        @ModelAttribute("income") IncomeDto incomeDto,
        @ModelAttribute("bindingResult") BindingResult bindingResult,
        Model model,
        Authentication authentication) {

        CustomUserDetails userDetails = currentUser(authentication);
        Map<UUID, String> apartmentIdNameMap = apartmentService.getApartmentIdNameMap(userDetails.getId());
        model.addAttribute("apartmentIdNameMap", apartmentIdNameMap);
        model.addAttribute("status", IncomeStatus.values());
        model.addAttribute("selectedStatus", incomeDto.getStatus());
        model.addAttribute("selectedApartment", apartmentIdNameMap.get(incomeDto.getApartmentId()));
        model.addAttribute("selectedTenant", tenantService.findById(incomeDto.getTenantId()));

        if (incomeDto == null) {
            incomeDto = new IncomeDto();
        }
        model.addAttribute("income", incomeDto);
        if (bindingResult.getObjectName() != null && bindingResult != null && bindingResult.hasErrors()) {
            model.addAttribute("org.springframework.validation.BindingResult.income", bindingResult);
            model.addAttribute("org.springframework.validation.BindingResult.bindingResult", bindingResult);
        }
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
            List<String> statusList = Stream.of(IncomeStatus.values())
                .map(Enum::toString)
                .toList();
            model.addAttribute("statusList", statusList);
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
