package com.landlordpro.service;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.landlordpro.domain.Expense;
import com.landlordpro.dto.ExpenseDto;
import com.landlordpro.mapper.ExpenseMapper;
import com.landlordpro.repository.ExpenseRepository;

import jakarta.transaction.Transactional;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;

    public ExpenseService(ExpenseRepository expenseRepository, ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
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
            List<Expense> expenses = expenseRepository.findByUserId(userId);
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
        Expense expense = expenseMapper.toEntity(expenseDto);
        try {
            expenseRepository.save(expense);
        } catch (DataIntegrityViolationException ex) {
            String errorMessage = "Constraint violation while saving expense=" + expenseDto.getId() + " User=" + expenseDto.getUserId();
            throw new RuntimeException(errorMessage, ex);
        } catch (Exception ex) {
            String errorMessage = "Unexpected error while saving expense=" + expenseDto.getId() + " User=" + expenseDto.getUserId();
            throw new RuntimeException(errorMessage, ex);
        }
    }

    public ExpenseDto findById(UUID id) {
        Expense expense = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Apartment not found"));
        return expenseMapper.toDTO(expense);
    }

    @Transactional
    public void deleteExpense(UUID id, UUID userId, UUID apartmentId) {
        expenseRepository.deleteByIdAndUserIdAndApartmentId(id, userId, apartmentId);
    }
}
