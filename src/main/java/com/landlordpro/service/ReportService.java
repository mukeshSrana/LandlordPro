package com.landlordpro.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.landlordpro.domain.ExpenseSummary;
import com.landlordpro.domain.IncomeSummary;
import com.landlordpro.domain.MonthlyIncomeReport;
import com.landlordpro.dto.IncomeExpenseSummaryDTO;
import com.landlordpro.dto.MonthlyIncomeReportDto;
import com.landlordpro.mapper.ReportMapper;
import com.landlordpro.repository.ExpenseRepository;
import com.landlordpro.repository.IncomeRepository;

@Service
public class ReportService {

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final ReportMapper reportMapper;

    public ReportService(IncomeRepository incomeRepository, ExpenseRepository expenseRepository, ReportMapper reportMapper) {
        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
        this.reportMapper = reportMapper;
    }

    public List<MonthlyIncomeReportDto> getMonthlyIncomeReport(UUID userId) {
        List<MonthlyIncomeReport> monthlyIncomeReports = incomeRepository.findMonthlyIncomeReport(userId);
        return reportMapper.toDTOList(monthlyIncomeReports);
    }

    public List<IncomeExpenseSummaryDTO> getIncomeExpenseSummary(UUID userId) {
        // Fetch income and expense summaries filtered by userId
        List<IncomeSummary> incomeSummaries = incomeRepository.findIncomeSummary(userId);
        List<ExpenseSummary> expenseSummaries = expenseRepository.findExpenseSummary(userId);

        // Map expense data by apartment name and year for quick lookup
        Map<String, Map<Integer, BigDecimal>> expenseMap = expenseSummaries.stream()
            .collect(Collectors.groupingBy(
                ExpenseSummary::getApartmentName, // Outer key: Apartment name
                Collectors.toMap(
                    ExpenseSummary::getYear,        // Inner key: Year
                    ExpenseSummary::getTotalExpenses // Value: Total expenses (no merge function needed)
                )
            ));

        // Combine income and expense data into the final DTO
        return incomeSummaries.stream()
            .map(income -> {
                Map<Integer, BigDecimal> yearlyExpenses = expenseMap.getOrDefault(income.getApartmentName(), Map.of());
                BigDecimal totalExpenses = yearlyExpenses.getOrDefault(income.getYear(), BigDecimal.ZERO);
                BigDecimal netIncome = income.getTotalIncome().subtract(totalExpenses);

                return new IncomeExpenseSummaryDTO(
                    income.getApartmentName(),
                    income.getYear(),
                    income.getTotalIncome(),
                    totalExpenses,
                    netIncome
                );
            })
            .collect(Collectors.toList());
    }

}
