package com.landlordpro.repository;

import com.landlordpro.domain.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

     List<Expense> findByUserId(UUID userId);
    void deleteByIdAndUserIdAndApartmentId(UUID id, UUID userId, UUID apartmentId);
    boolean existsByIdAndUserIdAndApartmentId(UUID id, UUID userId, UUID apartmentId);

    // Example: Find expenses by apartment ID
    // List<Expense> findByApartmentId(Integer apartmentId);

}

