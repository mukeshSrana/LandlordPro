package com.landlordpro.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.landlordpro.domain.Tenant;
import com.landlordpro.report.ApartmentOccupancySummary;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    @Query("SELECT t FROM Tenant t " +
        "WHERE t.apartment.id = :apartmentId " +
        "AND t.userId = :userId " +
        "AND (t.leaseEndDate IS NULL OR t.leaseEndDate >= CURRENT_DATE)")
    List<Tenant> findActiveTenantsByUserIdAndApartmentId(@Param("userId") UUID userId, @Param("apartmentId") UUID apartmentId);

    List<Tenant> findByUserIdAndApartmentId(UUID userId, UUID apartmentId);
    List<Tenant> findByUserId(UUID userId);
    List<Tenant> findByIdIn(List<UUID> tenantIds);

    @Query("SELECT new com.landlordpro.report.ApartmentOccupancySummary(" +
        "a.apartmentShortName, a.ownerName, COALESCE(t.fullName, 'N/A'), " +
        "t.leaseStartDate, t.leaseEndDate, " +
        "CASE " +
        "  WHEN t IS NULL THEN 'Vacant' " + // No tenant associated
        "  WHEN t.leaseEndDate IS NULL THEN 'Occupied' " + // Tenant with no lease end date
        "  WHEN t.leaseEndDate < CURRENT_DATE THEN 'Vacant' " + // Lease expired
        "  ELSE 'Occupied' " + // Active lease
        "END) " +
        "FROM Apartment a " +
        "LEFT JOIN Tenant t ON t.apartment.id = a.id " + // Reverse relationship
        "WHERE a.userId = :userId")
    List<ApartmentOccupancySummary> findApartmentOccupancyReport(@Param("userId") UUID userId);

    boolean existsByApartmentId(UUID id);

    boolean existsByIdAndUserIdAndApartmentId(UUID id, UUID userId, UUID apartmentId);
}
