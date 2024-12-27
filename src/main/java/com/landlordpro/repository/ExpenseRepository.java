package com.landlordpro.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.landlordpro.domain.Expense;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

     List<Expense> findByUserId(UUID userId);

    // Example: Find expenses by apartment ID
    // List<Expense> findByApartmentId(Integer apartmentId);

}

