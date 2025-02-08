package com.landlordpro.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.landlordpro.domain.Expense;
import com.landlordpro.dto.ExpenseDto;

@Mapper(componentModel = "spring", uses = MonetaryAmountMapper.class)
public interface ExpenseMapper {
    @Mappings({
        @Mapping(source = "amount", target = "amount", qualifiedByName = "monetaryAmountToBigDecimal")
    })
    ExpenseDto toDTO(Expense expense);

    @Mappings({
        @Mapping(source = "amount", target = "amount", qualifiedByName = "bigDecimalToMonetaryAmount")
    })
    Expense toEntity(ExpenseDto expenseDto);

    List<ExpenseDto> toDTOList(List<Expense> expenses);

    List<Expense> toEntityList(List<ExpenseDto> expenseDTOs);
}


