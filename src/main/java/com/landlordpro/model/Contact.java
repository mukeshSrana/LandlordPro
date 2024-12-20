package com.landlordpro.model;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL) // Avoid serializing null values
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contact {

    @JsonProperty("id")
    private String id = UUID.randomUUID().toString(); // Auto-generate a unique id using UUID

    @JsonProperty("name")
    @NotBlank(message = "Name is required")
    private String name;

    @JsonProperty("email")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address")
    private String email;

    @JsonProperty("message")
    @NotBlank(message = "Message is required")
    private String message;

    @JsonProperty("createdDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate = LocalDate.now(); // Date the apartment record was created
}
