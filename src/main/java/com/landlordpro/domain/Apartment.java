package com.landlordpro.domain;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "apartments")
@Data
@NoArgsConstructor
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "CHAR(36)") // UUID column definition to ensure it matches DB column type
    private UUID id;

    @Column(name = "apartment_short_name", nullable = false, length = 255)
    private String apartmentShortName;

    @Column(name = "owner_name", nullable = false, length = 100)
    private String ownerName;

    @Column(name = "address_line1", nullable = false, length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(name = "pincode", nullable = false, length = 10)  // Pincode should be non-null
    private String pincode;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;

    @Column(name = "updated_date")
    private LocalDate updatedDate; // Date the apartment record was last updated

    @Column(name = "user_id", nullable = false, columnDefinition = "CHAR(36)") // Foreign key column
    private UUID userId;

    @PrePersist
    public void prePersist() {
        if (this.createdDate == null) {
            this.createdDate = LocalDate.now();  // Set createdDate only when it's first persisted
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDate.now(); // Update the updatedDate when entity is updated
    }
}
