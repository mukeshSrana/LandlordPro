package com.landlordpro.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.landlordpro.domain.Income;
import com.landlordpro.report.IncomeSummary;
import com.landlordpro.report.MonthlyIncomeSummary;

@Repository
public interface IncomeRepository extends JpaRepository<Income, UUID> {

    boolean existsByIdAndUserIdAndApartmentId(UUID id, UUID userId, UUID apartmentId);
    void deleteByIdAndUserIdAndApartmentId(UUID id, UUID userId, UUID apartmentId);
    List<Income> findByUserId(UUID userId);

    @Query("SELECT new com.landlordpro.report.MonthlyIncomeSummary(" +
        "a.apartmentShortName, t.fullName, MONTH(i.date), YEAR(i.date), " +
        "SUM(i.amount), i.status) " +
        "FROM Income i " +
        "JOIN i.apartment a " +
        "JOIN i.tenant t " +
        "WHERE i.userId = :userId " +
        "GROUP BY a.apartmentShortName, t.fullName, MONTH(i.date), YEAR(i.date), i.status " +
        "ORDER BY YEAR(i.date), MONTH(i.date), a.apartmentShortName, t.fullName")
    List<MonthlyIncomeSummary> findMonthlyIncomeSummary(@Param("userId") UUID userId);

    @Query("SELECT new com.landlordpro.report.IncomeSummary(" +
        "a.apartmentShortName, SUM(i.amount), YEAR(i.date)) " +
        "FROM Income i JOIN i.apartment a " +
        "WHERE i.userId = :userId " +
        "GROUP BY YEAR(i.date), i.apartmentId " +
        "ORDER BY YEAR(i.date), i.apartmentId")
    List<IncomeSummary> findIncomeSummary(@Param("userId") UUID userId);

    boolean existsByApartmentId(UUID id);
}

