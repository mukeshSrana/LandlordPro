package com.landlordpro.dto;

public enum UserRole {
    ROLE_USER,
    ROLE_ADMIN,
    ROLE_MANAGER;

    @Override
    public String toString() {
        return name();
    }
}