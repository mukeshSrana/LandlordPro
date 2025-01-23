package com.landlordpro.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.landlordpro.domain.Income;
import com.landlordpro.dto.MonthlyIncomeReportDto;

@Repository
public interface IncomeRepository extends JpaRepository<Income, UUID> {

    @Query("SELECT new com.landlordpro.dto.MonthlyIncomeReportDto(" +
        "a.apartmentShortName, t.fullName, MONTH(i.date), YEAR(i.date), " +
        "SUM(i.amount), i.status) " +
        "FROM Income i " +
        "JOIN i.apartment a " +
        "JOIN i.tenant t " +
        "WHERE i.userId = :userId " +
        "GROUP BY a.apartmentShortName, t.fullName, MONTH(i.date), YEAR(i.date), i.status " +
        "ORDER BY YEAR(i.date), MONTH(i.date), a.apartmentShortName, t.fullName")
    List<MonthlyIncomeReportDto> findMonthlyIncomeReport(@Param("userId") UUID userId);
}

