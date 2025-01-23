package com.landlordpro.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.landlordpro.domain.report.MonthlyIncomeSummary;
import com.landlordpro.dto.report.MonthlyIncomeSummaryDto;

@Mapper(componentModel = "spring")
public interface ReportMapper {
    MonthlyIncomeSummaryDto toDTO(MonthlyIncomeSummary monthlyIncomeSummary);
    List<MonthlyIncomeSummaryDto> toDTOList(List<MonthlyIncomeSummary> monthlyIncomeSummaries);
}
