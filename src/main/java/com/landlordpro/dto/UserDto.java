package com.landlordpro.dto;

import java.time.LocalDateTime;
import java.util.UUID;

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
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Please enter a valid email address (e.g., user@example.com)")
    private String username;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Pattern(
        regexp = "^[a-zA-Z]+(?:\\s[a-zA-Z]+)*$",
        message = "Name must contain only letters and spaces"
    )
    private String name;

    @NotBlank(message = "Mobile number is required")
    @Pattern(
        regexp = "^\\+?[0-9]{8,12}$",
        message = "Mobile number must be a valid number with (8-12 digits, optional + at the beginning)"
    )
    private String mobileNumber;

    private String userRole;

    private boolean enabled;

    private boolean deleted;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}


