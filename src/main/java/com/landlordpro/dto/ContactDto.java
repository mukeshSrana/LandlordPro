package com.landlordpro.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.landlordpro.dto.constants.Patterns.EMAIL_PATTERN;
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
public class ContactDto {
    private UUID id;

    @NotBlank(message = "Reference is required")
    @Size(min = 10, max = 10, message = "Reference must be of 10 characters")
    private String reference;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Pattern(
        regexp = NAME_PATTERN_LETTER_AND_SPACES,
        message = "Name must contain only letters and spaces"
    )
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    @Pattern(regexp = EMAIL_PATTERN, message = "Please enter a valid email address (e.g., user@example.com)")
    private String email;

    @NotBlank(message = "Message is required")
    @Size(min = 10, max = 500, message = "Message should be at least 10 characters long")
    private String message;

    private boolean deleted;

    private boolean resolved;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}
