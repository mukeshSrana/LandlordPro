package com.landlordpro.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.landlordpro.domain.report.MonthlyIncomeSummary;
import com.landlordpro.dto.MonthlyIncomeReportDto;

@Mapper(componentModel = "spring")
public interface ReportMapper {
    MonthlyIncomeReportDto toDTO(MonthlyIncomeSummary monthlyIncomeSummary);
    List<MonthlyIncomeReportDto> toDTOList(List<MonthlyIncomeSummary> monthlyIncomeSummaries);
}
