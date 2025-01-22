package com.landlordpro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomeDto {

    private UUID id;  // UUID for income

    @NotNull(message = "Apartment ID is mandatory")  // Ensure it's not null
    private UUID apartmentId;  // Apartment ID

    @NotNull(message = "User ID is mandatory")  // Ensure it's not null
    private UUID userId;  // User ID (UUID)

    @NotNull(message = "Tenant ID is mandatory")  // Ensure it's not null
    private UUID tenantId;  // Tenant ID

    @NotNull(message = "Income date is mandatory")  // Ensure it's not null
    private LocalDate date;  // Date of income

    @NotNull(message = "Amount is mandatory")  // Ensure it's not null
    @Digits(integer = 10, fraction = 2, message = "Amount must be a valid monetary value")
    private BigDecimal amount;  // Income amount

    @NotBlank(message = "Payment status is mandatory")  // Ensure it's not blank
    @Size(max = 20, message = "Payment status should be up to 20 characters")  // Maximum length of 20
    private String status;  // Payment status

    @Size(max = 500, message = "Comments should be up to 500 characters")  // Maximum length of 500
    private String comments;  // Additional comments

    private byte[] receiptData;  // Binary data for the receipt

    private LocalDateTime createdDate;  // Date when the income was created

    private LocalDateTime updatedDate;  // Last updated date
}
