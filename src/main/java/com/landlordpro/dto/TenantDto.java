package com.landlordpro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;

import com.landlordpro.dto.validator.ValidAmount;
import com.landlordpro.dto.validator.ValidLeasePeriod;

import static com.landlordpro.dto.constants.Patterns.EMAIL_PATTERN;
import static com.landlordpro.dto.constants.Patterns.MOBILE_NR_PATTERN;
import static com.landlordpro.dto.constants.Patterns.NAME_PATTERN_LETTER_AND_SPACES;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ValidLeasePeriod
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantDto {

    private UUID id;
    private UUID userId;

    @NotNull(message = "Apartment ID is mandatory")
    private UUID apartmentId;

    @NotBlank(message = "Full name is mandatory")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    @Pattern(
        regexp = NAME_PATTERN_LETTER_AND_SPACES,
        message = "Full name must contain only letters and spaces"
    )
    private String fullName;

    @NotNull(message = "Date of birth is mandatory")
    @PastOrPresent(message = "Date of birth cannot be in the future")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Mobile number is required")
    @Pattern(
        regexp = MOBILE_NR_PATTERN,
        message = "Mobile number must be a valid number with (8-12 digits, optional + at the beginning)"
    )
    private String mobileNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    @Pattern(regexp = EMAIL_PATTERN, message = "Please enter a valid email address (e.g., user@example.com)")
    private String email;

    @NotNull(message = "Lease start date  is mandatory")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate leaseStartDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate leaseEndDate;

    @NotNull(message = "Monthly rent is mandatory")
    @ValidAmount
    private BigDecimal monthlyRent;

    @NotNull(message = "Security deposit is mandatory")
    @ValidAmount
    private BigDecimal securityDeposit;

    @NotBlank(message = "Security deposit institution is mandatory")
    @Size(min = 2, max = 100, message = "Security deposit institution must be between 2 and 100 characters")
    @Pattern(
        regexp = NAME_PATTERN_LETTER_AND_SPACES,
        message = "Security deposit institution must contain only letters and spaces"
    )
    private String securityDepositInstitutionName;

    private byte[] receiptData;

    private byte[] privatePolicy;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}
