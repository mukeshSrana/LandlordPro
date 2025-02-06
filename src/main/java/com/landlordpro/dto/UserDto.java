package com.landlordpro.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.landlordpro.dto.constants.Patterns.EMAIL_PATTERN;
import static com.landlordpro.dto.constants.Patterns.MOBILE_NR_PATTERN;
import static com.landlordpro.dto.constants.Patterns.NAME_PATTERN_LETTER_AND_SPACES;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private UUID id;

    @NotBlank(message = "Username is required")
    @Email(message = "Please enter a valid email address")
    @Pattern(regexp = EMAIL_PATTERN, message = "Please enter a valid email address (e.g., user@example.com)")
    private String username;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Pattern(
        regexp = NAME_PATTERN_LETTER_AND_SPACES,
        message = "Name must contain only letters and spaces"
    )
    private String name;

    @NotBlank(message = "Mobile number is required")
    @Pattern(
        regexp = MOBILE_NR_PATTERN,
        message = "Mobile number must be a valid number with (8-12 digits, optional + at the beginning)"
    )
    private String mobileNumber;

    private String userRole;

    private boolean enabled;

    private boolean deleted;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}


