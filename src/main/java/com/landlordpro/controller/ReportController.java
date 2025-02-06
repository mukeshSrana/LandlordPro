package com.landlordpro.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize("hasRole('ROLE_LANDLORD')")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/chart1")
    public String getReport1(Model model) {
        List<Integer> expenses = List.of(200, 400, 600, 500);
        List<String> months = List.of("Jan", "Feb", "Mar", "Apr");

        // Create the chart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < months.size(); i++) {
            dataset.addValue(expenses.get(i), "Expenses", months.get(i));
        }

        JFreeChart barChart = ChartFactory.createBarChart(
            "Monthly Expenses", "Month", "Amount", dataset);

        // Convert the chart to a Base64 image string
        try {
            BufferedImage chartImage = barChart.createBufferedImage(600, 400);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(chartImage, "png", baos);
            String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
            model.addAttribute("chartImage", base64Image);
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("expenses", expenses);
        model.addAttribute("months", months);
        model.addAttribute("page", "chartReport1");
        return "chartReport1"; // Thymeleaf template
    }


    @GetMapping("/chart")
    public String getReport(Model model) {
        // Example data for chart
        List<Integer> expenses = Arrays.asList(200, 400, 600, 500);
        List<String> months = Arrays.asList("Jan", "Feb", "Mar", "Apr");

        model.addAttribute("expenses", expenses);
        model.addAttribute("months", months);
        model.addAttribute("page", "chartReport");
        return "chartReport";
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
