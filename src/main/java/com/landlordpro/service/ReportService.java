package com.landlordpro.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.landlordpro.domain.MonthlyIncomeReport;
import com.landlordpro.dto.MonthlyIncomeReportDto;
import com.landlordpro.mapper.ReportMapper;
import com.landlordpro.repository.IncomeRepository;

@Service
public class ReportService {

    private final IncomeRepository incomeRepository;
    private final ReportMapper reportMapper;

    public ReportService(IncomeRepository incomeRepository, ReportMapper reportMapper) {
        this.incomeRepository = incomeRepository;
        this.reportMapper = reportMapper;
    }

    public List<MonthlyIncomeReportDto> getMonthlyIncomeReport(UUID userId) {
        List<MonthlyIncomeReport> monthlyIncomeReports = incomeRepository.findMonthlyIncomeReport(userId);
        return reportMapper.toDTOList(monthlyIncomeReports);
    }
}
