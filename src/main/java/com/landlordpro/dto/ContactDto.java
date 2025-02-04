package com.landlordpro.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address")
    private String email;

    @NotBlank(message = "Message is required")
    private String message;

    private boolean deleted;

    private boolean resolved;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}
