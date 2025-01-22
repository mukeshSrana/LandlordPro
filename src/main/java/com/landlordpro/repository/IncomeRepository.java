package com.landlordpro.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.landlordpro.domain.Income;

@Repository
public interface IncomeRepository extends JpaRepository<Income, UUID> {
}
