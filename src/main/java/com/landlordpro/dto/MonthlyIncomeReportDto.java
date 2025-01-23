package com.landlordpro.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyIncomeReportDto {
    private String apartmentName;
    private String tenantName;
    private int month;
    private int year;
    private BigDecimal totalIncome;
    private String paymentStatus;
}

