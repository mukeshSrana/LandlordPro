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
public class Apartment {

    @JsonProperty("id")
    private String id = UUID.randomUUID().toString(); // Auto-generate a unique id using UUID

    @JsonProperty("apartmentName")
    private String apartmentName;

    @JsonProperty("addressLine1")
    private String addressLine1;

    @JsonProperty("addressLine2")
    private String addressLine2;

    @JsonProperty("pincode")
    private String pincode;

    @JsonProperty("city")
    private String city;

    @JsonProperty("country")
    private String country;

    @JsonProperty("rentAmount")
    private BigDecimal rentAmount;

    @JsonProperty("leaseStartDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate leaseStartDate;

    @JsonProperty("leaseEndDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate leaseEndDate;

    @JsonProperty("tenantName")
    private String tenantName;

    @JsonProperty("contactNumber")
    private String contactNumber;

    @JsonProperty("email")
    private String email;

    @JsonProperty("createdDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate = LocalDate.now(); // Date the apartment record was created

    @JsonProperty("updatedDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate updatedDate; // Date the apartment record was last updated

    // Optional helper method to get lease duration in months
    @JsonProperty("leaseDurationMonths")
    public int getLeaseDurationMonths() {
        if (leaseStartDate != null && leaseEndDate != null) {
            return leaseStartDate.until(leaseEndDate).getMonths();
        }
        return 0; // Return 0 if lease dates are not available
    }
}
