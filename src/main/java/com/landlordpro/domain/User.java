package com.landlordpro.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.landlordpro.dto.enums.UserRole;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)  // Default generation strategy
    @Column(columnDefinition = "CHAR(36)")  // Ensure it matches the DB column type
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(name = "mobile_nr", nullable = false, length = 15)
    private String mobileNumber;

    @Column(name="accept_consent", nullable = false)
    private boolean acceptConsent;

    @Column(name="acceptTenantDataResponsibility", nullable = false)
    private boolean acceptTenantDataResponsibility;

    @Column(nullable = false)
    @AssertTrue
    private boolean enabled;

    @Column(name = "account_non_expired", nullable = false)
    private boolean accountNonExpired;

    @Column(name = "credentials_non_expired", nullable = false)
    private boolean credentialsNonExpired;

    @Column(name = "account_non_locked", nullable = false)
    private boolean accountNonLocked;

    @ElementCollection(fetch = FetchType.EAGER)  // Use EAGER fetch to load roles along with the user
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    public void addRole(UserRole role) {
        this.roles.add(role.name());
    }
}

