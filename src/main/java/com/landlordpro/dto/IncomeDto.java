package com.landlordpro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;

import com.landlordpro.dto.validator.ValidAmount;

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

    private UUID id;

    private UUID userId;

    @NotNull(message = "Apartment ID is mandatory")
    private UUID apartmentId;

    @NotNull(message = "Tenant ID is mandatory")
    private UUID tenantId;

    @NotNull(message = "Date is mandatory")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull(message = "Amount is mandatory")
    @ValidAmount
    private BigDecimal amount;

    @NotBlank(message = "Payment status is mandatory")
    private String status;

    @Size(max = 500, message = "Comments can be up to 200 characters")
    private String comments;

    private byte[] receiptData;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}
