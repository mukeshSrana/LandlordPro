package com.landlordpro.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tenants")
@Data
@NoArgsConstructor
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String mobileNumber;

    @Column(nullable = false)
    private String email;

    @Column(name = "user_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID userId;

    @ManyToOne
    @JoinColumn(name = "apartment_id", nullable = false, insertable = false, updatable = false)
    private Apartment apartment;

    @Column(name = "apartment_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID apartmentId;

    @Column(name = "lease_start_date", nullable = false)
    private LocalDate leaseStartDate;

    @Column(name = "lease_end_date")
    private LocalDate leaseEndDate;

    @Column(name = "monthly_rent", nullable = false, precision = 6, scale = 2)
    private BigDecimal monthlyRent;

    @Column(name = "security_deposit", precision = 6, scale = 2)
    private BigDecimal securityDeposit;

    @Column(name = "security_deposit_institution_name", length = 100)
    private String securityDepositInstitutionName;

    @Lob
    @Column(name = "receipt_data")
    private byte[] receiptData;

    @Lob
    @Column(name = "private_policy", nullable = false)
    private byte[] privatePolicy;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tenant tenant = (Tenant) o;

        // Trim and compare fullName (case-insensitive)
        String thisFullName = (fullName != null) ? fullName.trim() : null;
        String otherFullName = (tenant.fullName != null) ? tenant.fullName.trim() : null;

        return (thisFullName == null ? otherFullName == null : thisFullName.equalsIgnoreCase(otherFullName))
            && Objects.equals(dateOfBirth, tenant.dateOfBirth)
            && Objects.equals(mobileNumber, tenant.mobileNumber)
            && Objects.equals(apartmentId, tenant.apartmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            (fullName != null) ? fullName.trim().toLowerCase() : null,
            dateOfBirth,
            mobileNumber,
            apartmentId
        );
    }
}
