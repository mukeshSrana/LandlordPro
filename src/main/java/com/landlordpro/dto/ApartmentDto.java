package com.landlordpro.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentDto {

    private UUID id;  // UUID for apartment

    @NotBlank(message = "Apartment short name is mandatory")  // Ensure it's not blank
    @Size(max = 255, message = "Apartment short name should be up to 255 characters")  // Maximum length of 255
    private String apartmentShortName;

    @NotBlank(message = "Owner name is mandatory")  // Ensure it's not blank
    @Size(max = 100, message = "Owner name should be up to 100 characters")  // Maximum length of 100
    private String ownerName;

    @NotBlank(message = "Address line 1 is mandatory")  // Ensure it's not blank
    @Size(max = 255, message = "Address line 1 should be up to 255 characters")  // Maximum length of 255
    private String addressLine1;

    @Size(max = 255, message = "Address line 2 should be up to 255 characters")  // Optional but max length of 255
    private String addressLine2;

    @NotBlank(message = "Pincode is mandatory")  // Ensure it's not blank
    @Size(max = 10, message = "Pincode should be up to 10 characters")  // Maximum length of 10
    private String pincode;

    @NotBlank(message = "City is mandatory")  // Ensure it's not blank
    @Size(max = 100, message = "City should be up to 100 characters")  // Maximum length of 100
    private String city;

    @NotBlank(message = "Country is mandatory")  // Ensure it's not blank
    @Size(max = 100, message = "Country should be up to 100 characters")  // Maximum length of 100
    private String country;

    private LocalDateTime createdDate;  // Date apartment was created

    private LocalDateTime updatedDate;  // Last updated date

    private UUID userId;  // Foreign key to user (user_id)
}
