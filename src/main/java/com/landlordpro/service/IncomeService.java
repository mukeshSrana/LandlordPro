package com.landlordpro.service;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.landlordpro.domain.Income;
import com.landlordpro.dto.IncomeDto;
import com.landlordpro.mapper.IncomeMapper;
import com.landlordpro.repository.IncomeRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final IncomeMapper incomeMapper;

    public IncomeService(IncomeRepository incomeRepository, IncomeMapper incomeMapper) {
        this.incomeRepository = incomeRepository;
        this.incomeMapper = incomeMapper;
    }

    public void add(IncomeDto incomeDto) throws Exception {
        save(incomeDto);
    }

    private void save(IncomeDto incomeDto) throws RuntimeException {
       Income income = incomeMapper.toEntity(incomeDto);
        try {
            incomeRepository.save(income);
        } catch (DataIntegrityViolationException ex) {
            String errorMessage = "Constraint violation while saving income=" + incomeDto.getId() + " User=" + incomeDto.getUserId();
            log.error(errorMessage, ex); // Assuming you have a logger in place
            throw new RuntimeException(errorMessage, ex);
        } catch (Exception ex) {
            String errorMessage = "Unexpected error while saving income=" + incomeDto.getId() + " User=" + incomeDto.getUserId();
            log.error(errorMessage, ex); // Assuming you have a logger in place
            throw new RuntimeException(errorMessage, ex);
        }
    }

    public List<IncomeDto> getIncomeForUser(UUID userId) {
        try {
            // Validate the input
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }

            List<Income> incomes = incomeRepository.findByUserId(userId);
            return incomeMapper.toDTOList(incomes);
        } catch (EmptyResultDataAccessException ex) {
            // Handle specific database "no data found" case
            throw new RuntimeException("No income found for user with ID: " + userId, ex);

        } catch (IllegalArgumentException ex) {
            // Handle invalid input
            throw ex; // Rethrow, or handle as needed

        } catch (RuntimeException ex) {
            // Catch unexpected runtime exceptions
            throw new RuntimeException("Unexpected error occurred", ex);
        }
    }

    public IncomeDto findById(UUID id) {
        Income income = incomeRepository.findById(id).orElseThrow(() -> new RuntimeException("Apartment not found"));
        return incomeMapper.toDTO(income);
    }

    @Transactional
    public void deleteIncome(UUID id, UUID userId, UUID apartmentId) {
        incomeRepository.deleteByIdAndUserIdAndApartmentId(id, userId, apartmentId);
    }

    public void update(IncomeDto incomeDto, UUID userId) {
        if (!isExistsForUser(incomeDto.getId(), userId, incomeDto.getApartmentId())) {
            String errorMsg = "Income= " + incomeDto.getId() + " not exists for the logged-in user.";
            throw new RuntimeException(errorMsg);
        }

        save(incomeDto);
    }

    public boolean isExistsForUser(UUID id, UUID userId, UUID apartmentId) {
        return incomeRepository.existsByIdAndUserIdAndApartmentId(id, userId, apartmentId);
    }
}
