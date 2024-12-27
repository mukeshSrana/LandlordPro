package com.landlordpro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDto {

    private UUID id;  // UUID for expense

    @NotNull(message = "Apartment ID is mandatory")  // Ensure it's not null
    private UUID apartmentId;  // Apartment ID

    @NotNull(message = "User ID is mandatory")  // Ensure it's not null
    private UUID userId;  // User ID (UUID)

    @NotBlank(message = "Category is mandatory")  // Ensure it's not blank
    @Size(max = 255, message = "Category should be up to 255 characters")  // Maximum length of 255
    private String category;  // Expense category

    @NotBlank(message = "Expense name is mandatory")  // Ensure it's not blank
    @Size(max = 255, message = "Expense name should be up to 255 characters")  // Maximum length of 255
    private String name;  // Name of the expense

    @NotNull(message = "Amount is mandatory")  // Ensure it's not null
    private BigDecimal amount;  // Amount of the expense

    @NotBlank(message = "Expense location is mandatory")  // Ensure it's not blank
    @Size(max = 255, message = "Expense location should be up to 255 characters")  // Maximum length of 255
    private String expenseLocation;  // Location of the expense

    @NotNull(message = "Date is mandatory")  // Ensure it's not null
    private LocalDate date;  // Date of the expense

    private byte[] receiptData;  // Binary data for the receipt

    private LocalDateTime createdDate;  // Date when the expense was created

    private LocalDateTime updatedDate;  // Last updated date
}
