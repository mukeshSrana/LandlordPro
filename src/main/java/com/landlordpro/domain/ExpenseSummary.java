package com.landlordpro.domain;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseSummary {
    private String apartmentName;
    private BigDecimal totalExpenses;
    private Integer year;
}

