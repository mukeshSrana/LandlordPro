package com.landlordpro.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.landlordpro.dto.MonthlyIncomeReportDto;
import com.landlordpro.repository.IncomeRepository;

@Service
public class ReportService {

    private final IncomeRepository incomeRepository;

    public ReportService(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    public List<MonthlyIncomeReportDto> getMonthlyIncomeReport(UUID userId) {
        return incomeRepository.findMonthlyIncomeReport(userId);
    }
}
