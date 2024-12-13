package com.landlordpro.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL) // Avoid serializing null values
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    @JsonProperty("id")
    private String id = UUID.randomUUID().toString(); // Auto-generate a unique id using UUID

    @JsonProperty("name")
    private String name;

    @JsonProperty("apartmentName")
    private String apartmentName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("category")
    private String category;

    @JsonProperty("date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonProperty("attachmentPath")
    private String attachmentPath; // Path to associated receipts/bills

    @JsonProperty("year")
    public String getYear() {
        if (date != null) {
            return String.valueOf(date.getYear());
        }
        return "0"; // or another default string value
    }

}
