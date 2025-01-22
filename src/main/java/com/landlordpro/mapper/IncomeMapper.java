package com.landlordpro.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.landlordpro.domain.Income;
import com.landlordpro.dto.IncomeDto;

@Mapper(componentModel = "spring")
public interface IncomeMapper {

    // Mapping a single Income entity to IncomeDto
    IncomeDto toDTO(Income income);

    // Mapping a single IncomeDto to Income entity
    Income toEntity(IncomeDto incomeDto);

    // Mapping a list of Income entities to a list of IncomeDto
    List<IncomeDto> toDTOList(List<Income> incomes);

    // Mapping a list of IncomeDto to a list of Income entities
    List<Income> toEntityList(List<IncomeDto> incomeDTOs);
}

