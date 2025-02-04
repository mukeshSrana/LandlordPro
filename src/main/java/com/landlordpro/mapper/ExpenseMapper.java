package com.landlordpro.mapper;

import com.landlordpro.domain.Expense;
import com.landlordpro.dto.ExpenseDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    // Mapping a single Expense entity to ExpenseDto
    ExpenseDto toDTO(Expense expense);

    // Mapping a single ExpenseDto to Expense entity
    Expense toEntity(ExpenseDto expenseDto);

    // Mapping a list of Expense entities to a list of ExpenseDto
    List<ExpenseDto> toDTOList(List<Expense> expenses);

    // Mapping a list of ExpenseDto to a list of Expense entities
    List<Expense> toEntityList(List<ExpenseDto> expenseDTOs);
}

