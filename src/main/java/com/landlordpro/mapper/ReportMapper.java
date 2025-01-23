package com.landlordpro.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.landlordpro.report.MonthlyIncomeSummary;
import com.landlordpro.report.MonthlyIncomeSummaryDto;

@Mapper(componentModel = "spring")
public interface ReportMapper {
    MonthlyIncomeSummaryDto toDTO(MonthlyIncomeSummary monthlyIncomeSummary);
    List<MonthlyIncomeSummaryDto> toDTOList(List<MonthlyIncomeSummary> monthlyIncomeSummaries);
}
