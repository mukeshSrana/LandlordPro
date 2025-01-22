package com.landlordpro.dto;

public enum IncomeStatus {
    COMPLETED("completed"),
    PENDING("pending"),
    OVERDUE("overdue");

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