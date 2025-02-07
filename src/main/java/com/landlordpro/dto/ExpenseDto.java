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

    private UUID id;

    @NotNull(message = "Apartment ID is mandatory")
    private UUID apartmentId;

    @NotNull(message = "User ID is mandatory")
    private UUID userId;

    @NotBlank(message = "Category is mandatory")
    @Size(max = 255, message = "Category should be up to 255 characters")
    private String category;

    @NotBlank(message = "Expense name is mandatory")
    @Size(max = 255, message = "Expense name should be up to 255 characters")
    private String name;

    @NotNull(message = "Amount is mandatory")
    private BigDecimal amount;

    @NotBlank(message = "Expense location is mandatory")
    @Size(max = 255, message = "Expense location should be up to 255 characters")
    private String expenseLocation;

    @NotNull(message = "Date is mandatory")
    private LocalDate date;

    private byte[] receiptData;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}
