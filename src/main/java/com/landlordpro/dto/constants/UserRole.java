package com.landlordpro.dto.constants;

public enum UserRole {
    ROLE_LANDLORD("Landlord"),
    ROLE_ADMIN("Admin"),
    ROLE_MANAGER("Manager");

    private final String description;

    UserRole(String description) {
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