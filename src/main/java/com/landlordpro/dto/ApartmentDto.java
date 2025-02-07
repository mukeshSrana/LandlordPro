package com.landlordpro.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.landlordpro.dto.constants.Patterns.ADDRESS1_PATTERN_LETTER_AND_SPACES;
import static com.landlordpro.dto.constants.Patterns.ADDRESS2_PATTERN_LETTER_AND_SPACES;
import static com.landlordpro.dto.constants.Patterns.APARTMENT_NAME_PATTERN_LETTER_WITHOUT_SPACES;
import static com.landlordpro.dto.constants.Patterns.CITY_PATTERN_LETTER_AND_SPACES;
import static com.landlordpro.dto.constants.Patterns.NAME_PATTERN_LETTER_AND_SPACES;
import static com.landlordpro.dto.constants.Patterns.PINCODE_PATTERN_FOUR_DIGITS;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentDto {
    private UUID id;

    @NotBlank(message = "Apartment short name is mandatory")
    @Size(min = 2, max = 40, message = "Apartment short name must be between 2 and 40 characters")
    @Pattern(
        regexp = APARTMENT_NAME_PATTERN_LETTER_WITHOUT_SPACES,
        message = "Name must contain only letters without spaces or special characters"
    )
    private String apartmentShortName;

    @NotBlank(message = "Owner name is mandatory")
    @Size(min = 2, max = 100, message = "Owner name must be between 2 and 100 characters")
    @Pattern(
        regexp = NAME_PATTERN_LETTER_AND_SPACES,
        message = "Owner name must contain only letters and spaces"
    )
    private String ownerName;

    @NotBlank(message = "AddressLine1 is mandatory")
    @Size(min = 2, max = 100, message = "AddressLine1 must be between 2 and 100 characters")
    @Pattern(
        regexp = ADDRESS1_PATTERN_LETTER_AND_SPACES,
        message = "AddressLine1 must contain only letters, spaces or special characters"
    )
    private String addressLine1;

    @Size(max = 100, message = "AddressLine2 must be between 0 and 100 characters")
    @Pattern(
        regexp = ADDRESS2_PATTERN_LETTER_AND_SPACES,
        message = "AddressLine2 must contain only letters, spaces or special characters"
    )
    private String addressLine2;

    @NotBlank(message = "Pincode is mandatory")
    @Size(min = 4, max = 4, message = "Pincode must be of 4 digits")
    @Pattern(
        regexp = PINCODE_PATTERN_FOUR_DIGITS,
        message = "Pincode must contain only 4 digits"
    )
    private String pincode;

    @NotBlank(message = "City is mandatory")
    @Size(max = 100, message = "City should be up to 100 characters")
    @Pattern(
        regexp = CITY_PATTERN_LETTER_AND_SPACES,
        message = "City must contain only letters and spaces"
    )
    private String city;

    @NotBlank(message = "Country is mandatory")
    @Size(max = 100, message = "Country should be up to 100 characters")
    @Pattern(
        regexp = NAME_PATTERN_LETTER_AND_SPACES,
        message = "Country must be valid and contain only letters and spaces"
    )
    private String country;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    private UUID userId;
}
