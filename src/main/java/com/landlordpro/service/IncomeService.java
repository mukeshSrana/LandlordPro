package com.landlordpro.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.landlordpro.domain.Income;
import com.landlordpro.dto.IncomeDto;
import com.landlordpro.mapper.IncomeMapper;
import com.landlordpro.repository.IncomeRepository;

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
            String errorMessage = "Constraint violation while saving expense=" + incomeDto.getId() + " User=" + incomeDto.getUserId();
            log.error(errorMessage, ex); // Assuming you have a logger in place
            throw new RuntimeException(errorMessage, ex);
        } catch (Exception ex) {
            String errorMessage = "Unexpected error while saving expense=" + incomeDto.getId() + " User=" + incomeDto.getUserId();
            log.error(errorMessage, ex); // Assuming you have a logger in place
            throw new RuntimeException(errorMessage, ex);
        }
    }
}
