package com.landlordpro.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.landlordpro.domain.Apartment;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, UUID> {
    @Query("SELECT a.apartmentShortName FROM Apartment a WHERE a.userId = :userId")
    List<String> findApartmentNamesByUserId(@Param("userId") UUID userId);
    void deleteByIdAndUserId(UUID id, UUID userId);

    List<Apartment> findByUserId(UUID userId);

    boolean existsByApartmentShortNameAndUserId(String apartmentShortName, UUID userId);

    boolean existsByApartmentShortNameIgnoreCaseAndUserId(String apartmentShortName, UUID userId);

    boolean existsByIdAndUserId(UUID apartmentId, UUID userId);

    List<Apartment> findByIdIn(List<UUID> apartmentIds);

    Optional<Apartment> findByIdAndUserId(UUID id, UUID userId);
}
