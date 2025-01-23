package com.landlordpro.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.landlordpro.domain.MonthlyIncomeReport;
import com.landlordpro.dto.MonthlyIncomeReportDto;

@Mapper(componentModel = "spring")
public interface ReportMapper {
    MonthlyIncomeReportDto toDTO(MonthlyIncomeReport monthlyIncomeReport);
    List<MonthlyIncomeReportDto> toDTOList(List<MonthlyIncomeReport> monthlyIncomeReports);
}
