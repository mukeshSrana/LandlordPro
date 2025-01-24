package com.landlordpro.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.landlordpro.report.ApartmentOccupancySummary;
import com.landlordpro.report.MonthlyIncomeSummary;
import com.landlordpro.report.NetYieldSummary;
import com.landlordpro.security.CustomUserDetails;
import com.landlordpro.service.ReportService;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
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
