package com.landlordpro.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "incomes")
@Data
@NoArgsConstructor
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "CHAR(36)") // UUID column definition to ensure it matches DB column type
    private UUID id;

    @Column(name = "apartment_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID apartmentId;

    @Column(name = "user_id", nullable = false, columnDefinition = "CHAR(36)") // UUID column for user
    private UUID userId;

    @Column(name = "tenant_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID tenantId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 20)
    private String status; // e.g., completed, pending, overdue

    @Column(length = 500)
    private String comments;

    @Lob
    @Column(name = "receipt_data")
    private byte[] receiptData; // Store the uploaded receipt as binary data (BLOB)

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    // Lifecycle methods to auto-populate timestamps
    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}

