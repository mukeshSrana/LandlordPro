package com.landlordpro.report;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomeSummary {
    private String apartmentName;
    private BigDecimal totalIncome;
    private Integer year;
}

