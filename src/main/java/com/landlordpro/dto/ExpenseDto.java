package com.landlordpro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;

import com.landlordpro.dto.validator.ValidAmount;

import static com.landlordpro.dto.constants.Patterns.NAME_PATTERN_LETTER_AND_SPACES;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDto {

    private UUID id;

    private UUID userId;

    @NotNull(message = "Apartment ID is mandatory")
    private UUID apartmentId;

    @NotBlank(message = "Category is mandatory")
    @Size(max = 100, message = "Category should be up to 100 characters")
    private String category;

    @NotBlank(message = "Expense name is mandatory")
    @Size(min = 2, max = 100, message = "Expense name must be between 2 and 100 characters")
    @Pattern(
        regexp = NAME_PATTERN_LETTER_AND_SPACES,
        message = "Expense name must contain only letters and spaces"
    )
    private String name;

    @NotNull(message = "Amount is mandatory")
    @ValidAmount
    private BigDecimal amount;

    @NotBlank(message = "Expense location is mandatory")
    @Size(min = 2, max = 100, message = "Expense location must be between 2 and 100 characters")
    @Pattern(
        regexp = NAME_PATTERN_LETTER_AND_SPACES,
        message = "Expense location must contain only letters and spaces"
    )
    private String expenseLocation;

    @NotNull(message = "Date is mandatory")
    @PastOrPresent(message = "Date cannot be in the future")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private byte[] receiptData;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}
