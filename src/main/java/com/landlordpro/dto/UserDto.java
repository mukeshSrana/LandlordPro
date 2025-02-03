package com.landlordpro.dto;

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

    @NotBlank(message = "Username is required")
    @Email(message = "Invalid email format")
    private String username;

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must not exceed 50 characters")
    private String name;

    @NotBlank(message = "Mobile number is required")
    @Pattern(
        regexp = "^\\d{10,15}$",
        message = "Mobile number must be a valid number with 10-15 digits"
    )
    private String mobileNumber;

    private String userRole; // Comma-separated roles

    private boolean enabled;

    private boolean deleted;

    private UUID id;
}


