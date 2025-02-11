package com.landlordpro.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.money.Monetary;
import javax.money.MonetaryAmount;

import com.landlordpro.mapper.MonetaryAmountMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "expenses")
@Data
@NoArgsConstructor
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "CHAR(36)") // UUID column definition to ensure it matches DB column type
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartment_id", nullable = false, insertable = false, updatable = false)
    private Apartment apartment;

    @Column(name = "apartment_id", nullable = false, columnDefinition = "CHAR(36)", updatable = false)
    private UUID apartmentId;

    @Column(name = "user_id", nullable = false, columnDefinition = "CHAR(36)", updatable = false)
    private UUID userId;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    @Convert(converter = MonetaryAmountConverter.class)
    private MonetaryAmount amount;

    @Column(name = "expense_location", nullable = false, length = 100)
    private String expenseLocation;

    @Column(nullable = false)
    private LocalDate date;

    @Lob
    @Column(name = "receipt_data")
    private byte[] receiptData;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }

    public static class MonetaryAmountConverter implements AttributeConverter<MonetaryAmount, BigDecimal> {
        @Override
        public BigDecimal convertToDatabaseColumn(MonetaryAmount attribute) {
            return attribute != null ? attribute.getNumber().numberValue(BigDecimal.class) : null;
        }

        @Override
        public MonetaryAmount convertToEntityAttribute(BigDecimal dbData) {
            return dbData != null ? Monetary.getDefaultAmountFactory()
                .setCurrency(MonetaryAmountMapper.validCurrency)
                .setNumber(dbData)
                .create() : null;
        }
    }
}
