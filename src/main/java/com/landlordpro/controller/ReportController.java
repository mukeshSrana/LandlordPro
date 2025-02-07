package com.landlordpro.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.landlordpro.report.ApartmentOccupancySummary;
import com.landlordpro.report.MonthlyIncomeSummary;
import com.landlordpro.report.NetYieldSummary;
import com.landlordpro.security.CustomUserDetails;
import com.landlordpro.service.ReportService;
import com.landlordpro.service.chart.ChartService;

@Controller
@RequestMapping("/reports")
@PreAuthorize("hasRole('ROLE_LANDLORD')")
public class ReportController {

    private final ReportService reportService;
    private final ChartService chartService;

    public ReportController(ReportService reportService, ChartService chartService) {
        this.reportService = reportService;
        this.chartService = chartService;
    }

    @GetMapping("/monthlyExpense")
    public String showChartReport(@RequestParam(defaultValue = "line") String chartType, Model model) throws IOException {
        List<Integer> expenses = Arrays.asList(200, 400, 600, 500);
        List<String> months = Arrays.asList("Jan", "Feb", "Mar", "Apr");

        if (!"table".equals(chartType)) {
            byte[] chartImage = chartService.generateChartImage(chartType, expenses, months);
            String base64Image = Base64.getEncoder().encodeToString(chartImage);
            model.addAttribute("chartImage", "data:image/png;base64," + base64Image);
        }

        model.addAttribute("chartType", chartType);
        model.addAttribute("expenses", expenses);
        model.addAttribute("months", months);
        model.addAttribute("page", "monthlyExpenseReport");

        return "monthlyExpenseReport";
    }

    @GetMapping("/apartmentOccupancy")
    public String apartmentOccupancyReport(Authentication authentication, Model model) {
        CustomUserDetails userDetails = currentUser(authentication);

        List<ApartmentOccupancySummary> reportData = reportService.getApartmentOccupancyReport(userDetails.getId());
        model.addAttribute("reportData", reportData);
        model.addAttribute("page", "apartmentOccupancyReport");

        return "apartmentOccupancyReport";
    }

    @GetMapping("/monthlyIncome")
    public String monthlyIncomeReport(Authentication authentication, Model model) {
        CustomUserDetails userDetails = currentUser(authentication);

        List<MonthlyIncomeSummary> reportData = reportService.getMonthlyIncomeReport(userDetails.getId());
        model.addAttribute("reportData", reportData);
        model.addAttribute("page", "monthlyIncomeReport");

        return "monthlyIncomeReport";
    }

    @GetMapping("/netYield")
    public String netYieldReport(Authentication authentication, Model model) {

        CustomUserDetails userDetails = currentUser(authentication);

        // Fetch the report data filtered by userId
        List<NetYieldSummary> reportData = reportService.getNetYieldReport(userDetails.getId());

        model.addAttribute("reportData", reportData);
        model.addAttribute("page", "netYieldReport");

        return "netYieldReport";
    }

    private CustomUserDetails currentUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            return customUserDetails;
        }
        throw new IllegalStateException("Unexpected principal type");
    }
}
