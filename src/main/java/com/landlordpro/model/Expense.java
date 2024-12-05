package com.landlordpro.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    @JsonProperty("id")
    private String id;

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
    public int getYear() {
        if (date != null) {
            return date.getYear();
        }
        return 0; // or another default value
    }
}
