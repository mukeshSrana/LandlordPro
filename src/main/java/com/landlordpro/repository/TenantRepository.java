package com.landlordpro.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.landlordpro.domain.Tenant;
import com.landlordpro.report.ApartmentOccupancySummary;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    List<Tenant> findByUserIdAndApartmentId(UUID userId, UUID apartmentId);
    List<Tenant> findByUserId(UUID userId);

    @Query("SELECT new com.landlordpro.report.ApartmentOccupancySummary(" +
        "a.apartmentShortName, a.ownerName, t.fullName, t.leaseStartDate, t.leaseEndDate, " +
        "CASE " +
        "  WHEN t.leaseEndDate IS NULL THEN 'Occupied' " +
        "  WHEN t.leaseEndDate < CURRENT_DATE THEN 'Vacant' " +
        "  ELSE 'Occupied' " +
        "END) " +
        "FROM Tenant t JOIN t.apartment a " +
        "WHERE a.userId = :userId")
    List<ApartmentOccupancySummary> findApartmentOccupancyReport(@Param("userId") UUID userId);
}
