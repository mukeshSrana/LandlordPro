package com.landlordpro.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    private String reference;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;

    @NotBlank(message = "Message is required")
    @Size(min = 10, message = "Message should be at least 10 characters long")
    private String message;

    private boolean deleted;

    private boolean resolved;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}
