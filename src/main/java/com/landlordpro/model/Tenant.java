package com.landlordpro.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL) // Avoid serializing null values
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {

    @JsonProperty("id")
    private String id = UUID.randomUUID().toString(); // Auto-generate a unique id using UUID

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("dateOfBirth")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonProperty("email")
    private String email;

    @JsonProperty("apartmentId")
    private String apartmentId; // Foreign key reference to Apartment

    @JsonProperty("apartmentName")
    private String apartmentName;

    @JsonProperty("leaseStartDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate leaseStartDate;

    @JsonProperty("leaseEndDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate leaseEndDate;

    @JsonProperty("monthlyRent")
    private BigDecimal monthlyRent;

    @JsonProperty("securityDeposit")
    private BigDecimal securityDeposit;

    @JsonProperty("securityDepositInstitutionName")
    private String securityDepositInstitutionName;

    @JsonProperty("createdDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate = LocalDate.now(); // Date the tenant record was created

    @JsonProperty("updatedDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate updatedDate; // Date the tenant record was last updated

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tenant tenant = (Tenant) o;
        return Objects.equals(fullName, tenant.fullName) && Objects.equals(dateOfBirth, tenant.dateOfBirth)
            && Objects.equals(phoneNumber, tenant.phoneNumber) && Objects.equals(apartmentId, tenant.apartmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, dateOfBirth, phoneNumber, apartmentId);
    }
}

