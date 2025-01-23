package com.landlordpro.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.landlordpro.domain.Tenant;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    List<Tenant> findByUserIdAndApartmentId(UUID userId, UUID apartmentId);
    List<Tenant> findByUserId(UUID userId);
}
