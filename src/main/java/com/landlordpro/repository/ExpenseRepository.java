package com.landlordpro.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.landlordpro.domain.Expense;
import com.landlordpro.report.ExpenseSummary;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    List<Expense> findByUserId(UUID userId);
    void deleteByIdAndUserIdAndApartmentId(UUID id, UUID userId, UUID apartmentId);
    boolean existsByIdAndUserIdAndApartmentId(UUID id, UUID userId, UUID apartmentId);

    @Query("SELECT new com.landlordpro.report.ExpenseSummary(" +
        "a.apartmentShortName, SUM(e.amount), YEAR(e.date)) " +
        "FROM Expense e JOIN e.apartment a " +
        "WHERE a.userId = :userId " +
        "GROUP BY YEAR(e.date), e.apartmentId " +
        "ORDER BY YEAR(e.date), e.apartmentId")
    List<ExpenseSummary> findExpenseSummary(@Param("userId") UUID userId);

    boolean existsByApartmentId(UUID id);
}

