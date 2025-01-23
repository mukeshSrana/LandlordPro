package com.landlordpro.domain.report;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyIncomeSummary {
    private String apartmentName;
    private String tenantName;
    private int month;
    private int year;
    private BigDecimal totalIncome;
    private String paymentStatus;
}

