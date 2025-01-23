package com.landlordpro.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.landlordpro.dto.ExpenseDto;
import com.landlordpro.mapper.ExpenseMapper;
import com.landlordpro.repository.ExpenseRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExpenseService {
    private final Path basePath = Paths.get("./data/expenses");
    private final ObjectMapper objectMapper;

    @Value("${app.file-storage.base-dir}")
    private String fileStorageBaseDir;

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final ApartmentService apartmentService;

    public ExpenseService(ObjectMapper objectMapper, ExpenseRepository expenseRepository, ExpenseMapper expenseMapper,
        ApartmentService apartmentService) {
        this.objectMapper = objectMapper;
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
        this.apartmentService = apartmentService;
    }

    public void update(ExpenseDto expenseDto, UUID userId) throws RuntimeException {
        if (!isExistsForUser(expenseDto.getId(), userId, expenseDto.getApartmentId())) {
            String errorMsg = "Expense= " + expenseDto.getName() + " not exists for the logged-in user.";
            throw new RuntimeException(errorMsg);
        }

        save(expenseDto);
    }

    public boolean isExistsForUser(UUID id, UUID userId, UUID apartmentId) {
        return expenseRepository.existsByIdAndUserIdAndApartmentId(id, userId, apartmentId);
    }

    public List<ExpenseDto> getExpensesForUser(UUID userId) {
        try {
            // Validate the input
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }

            // Fetch expenses
            List<com.landlordpro.domain.Expense> expenses = expenseRepository.findByUserId(userId);
            return expenseMapper.toDTOList(expenses);
        } catch (EmptyResultDataAccessException ex) {
            // Handle specific database "no data found" case
            throw new RuntimeException("No expenses found for user with ID: " + userId, ex);

        } catch (IllegalArgumentException ex) {
            // Handle invalid input
            throw ex; // Rethrow, or handle as needed

        } catch (RuntimeException ex) {
            // Catch unexpected runtime exceptions
            throw new RuntimeException("Unexpected error occurred", ex);
        }
    }


    public void add(ExpenseDto expenseDto) throws Exception{
        save(expenseDto);
    }

    private void save(ExpenseDto expenseDto) throws RuntimeException {
        com.landlordpro.domain.Expense expense = expenseMapper.toEntity(expenseDto);
        try {
            expenseRepository.save(expense);
        } catch (DataIntegrityViolationException ex) {
            String errorMessage = "Constraint violation while saving expense=" + expenseDto.getId() + " User=" + expenseDto.getUserId();
            log.error(errorMessage, ex); // Assuming you have a logger in place
            throw new RuntimeException(errorMessage, ex);
        } catch (Exception ex) {
            String errorMessage = "Unexpected error while saving expense=" + expenseDto.getId() + " User=" + expenseDto.getUserId();
            log.error(errorMessage, ex); // Assuming you have a logger in place
            throw new RuntimeException(errorMessage, ex);
        }
    }

    public ExpenseDto findById(UUID id) {
        com.landlordpro.domain.Expense expense = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Apartment not found"));
        return expenseMapper.toDTO(expense);
    }

    @Transactional
    public void deleteExpense(UUID id, UUID userId, UUID apartmentId) {
        expenseRepository.deleteByIdAndUserIdAndApartmentId(id, userId, apartmentId);
    }
}
