package com.landlordpro.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;

import com.landlordpro.report.ApartmentOccupancySummary;
import com.landlordpro.report.ExpenseSummary;
import com.landlordpro.report.IncomeSummary;
import com.landlordpro.report.MonthlyIncomeSummary;
import com.landlordpro.report.NetYieldSummary;
import com.landlordpro.repository.ExpenseRepository;
import com.landlordpro.repository.IncomeRepository;
import com.landlordpro.repository.TenantRepository;

@Service
public class ReportService {

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final TenantRepository tenantRepository;

    public ReportService(IncomeRepository incomeRepository, ExpenseRepository expenseRepository, TenantRepository tenantRepository) {
        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
        this.tenantRepository = tenantRepository;
    }

    public byte[] generateExpenseChart(List<Integer> expenses, List<String> months) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < months.size(); i++) {
            dataset.addValue(expenses.get(i), "Expenses", months.get(i));
        }

        JFreeChart chart = ChartFactory.createBarChart(
            "Monthly Expenses",
            "Month",
            "Amount",
            dataset
        );

        chart.setBackgroundPaint(Color.white);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(baos, chart, 800, 400);
        return baos.toByteArray();
    }

    public List<MonthlyIncomeSummary> getMonthlyIncomeReport(UUID userId) {
        return incomeRepository.findMonthlyIncomeSummary(userId);
    }

    public List<ApartmentOccupancySummary> getApartmentOccupancyReport(UUID userId) {
        return tenantRepository.findApartmentOccupancyReport(userId);
    }

    public List<NetYieldSummary> getNetYieldReport(UUID userId) {
        // Fetch income and expense summaries filtered by userId
        List<IncomeSummary> incomeSummaries = incomeRepository.findIncomeSummary(userId);
        List<ExpenseSummary> expenseSummaries = expenseRepository.findExpenseSummary(userId);

        // Map income and expense data by apartment name and year
        Map<String, Map<Integer, BigDecimal>> incomeMap = incomeSummaries.stream()
            .collect(Collectors.groupingBy(
                IncomeSummary::getApartmentName,
                Collectors.toMap(
                    IncomeSummary::getYear,
                    IncomeSummary::getTotalIncome
                )
            ));

        Map<String, Map<Integer, BigDecimal>> expenseMap = expenseSummaries.stream()
            .collect(Collectors.groupingBy(
                ExpenseSummary::getApartmentName,
                Collectors.toMap(
                    ExpenseSummary::getYear,
                    ExpenseSummary::getTotalExpenses
                )
            ));

        // Collect all unique apartment-year combinations
        Set<String> apartmentNames = new HashSet<>();
        apartmentNames.addAll(incomeMap.keySet());
        apartmentNames.addAll(expenseMap.keySet());

        Set<Pair<String, Integer>> apartmentYearPairs = new HashSet<>();
        for (String apartmentName : apartmentNames) {
            Set<Integer> incomeYears = incomeMap.getOrDefault(apartmentName, Map.of()).keySet();
            Set<Integer> expenseYears = expenseMap.getOrDefault(apartmentName, Map.of()).keySet();
            Set<Integer> allYears = new HashSet<>();
            allYears.addAll(incomeYears);
            allYears.addAll(expenseYears);

            for (Integer year : allYears) {
                apartmentYearPairs.add(Pair.of(apartmentName, year));
            }
        }

        // Combine income and expense data into the final DTO
        return apartmentYearPairs.stream()
            .map(pair -> {
                String apartmentName = pair.getLeft();
                Integer year = pair.getRight();

                BigDecimal totalIncome = incomeMap
                    .getOrDefault(apartmentName, Map.of())
                    .getOrDefault(year, BigDecimal.ZERO);

                BigDecimal totalExpenses = expenseMap
                    .getOrDefault(apartmentName, Map.of())
                    .getOrDefault(year, BigDecimal.ZERO);

                BigDecimal netIncome = totalIncome.subtract(totalExpenses);

                BigDecimal netYield = (totalIncome.compareTo(BigDecimal.ZERO) > 0)
                    ? netIncome.multiply(BigDecimal.valueOf(100)).divide(totalIncome, 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

                return new NetYieldSummary(
                    apartmentName,
                    year,
                    totalIncome,
                    totalExpenses,
                    netIncome,
                    netYield
                );
            })
            .sorted(Comparator.comparing(NetYieldSummary::getYear)
                .thenComparing(NetYieldSummary::getApartmentName))
            .collect(Collectors.toList());
    }
}
