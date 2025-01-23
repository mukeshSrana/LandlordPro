package com.landlordpro.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
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

                return new IncomeExpenseSummaryDTO(
                    apartmentName,
                    year,
                    totalIncome,
                    totalExpenses,
                    netIncome
                );
            })
            .sorted(Comparator.comparing(IncomeExpenseSummaryDTO::getYear)
                .thenComparing(IncomeExpenseSummaryDTO::getApartmentName))
            .collect(Collectors.toList());
    }
}
