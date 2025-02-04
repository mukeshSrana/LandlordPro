package com.landlordpro.dto.enums;

public enum IncomeStatus {
    COMPLETED("Completed"),
    PENDING("Pending"),
    OVERDUE("Overdue");

    private final String description;

    IncomeStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}