package com.landlordpro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantDto {

    private UUID id;  // UUID for tenant

    @NotBlank(message = "Full name is mandatory")
    @Size(max = 255, message = "Full name must be up to 255 characters")
    private String fullName;  // Full name of the tenant

    @NotNull(message = "Date of birth is mandatory")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;  // Date of birth

    @NotBlank(message = "Phone number is mandatory")
    @Size(max = 15, message = "Phone number must be up to 15 characters")
    private String phoneNumber;  // Phone number

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must be up to 255 characters")
    private String email;  // Email address

    @NotNull(message = "User ID is mandatory")
    private UUID userId;  // User ID

    @NotNull(message = "Apartment ID is mandatory")
    private UUID apartmentId;  // Apartment ID

    @NotNull(message = "Lease start date is mandatory")
    private LocalDate leaseStartDate;  // Lease start date

    private LocalDate leaseEndDate;  // Lease end date

    @NotNull(message = "Monthly rent is mandatory")
    @Digits(integer = 10, fraction = 2, message = "Monthly rent must be a valid monetary value")
    private BigDecimal monthlyRent;  // Monthly rent

    @Digits(integer = 10, fraction = 2, message = "Security deposit must be a valid monetary value")
    private BigDecimal securityDeposit;  // Security deposit amount

    @Size(max = 255, message = "Security deposit institution name must be up to 255 characters")
    private String securityDepositInstitutionName;  // Institution name for the deposit

    private byte[] receiptData;  // Binary data for the receipt

    private LocalDateTime createdDate;  // Date when the tenant record was created

    private LocalDateTime updatedDate;  // Last updated date
}
