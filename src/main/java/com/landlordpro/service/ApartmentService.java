package com.landlordpro.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.landlordpro.domain.Apartment;
import com.landlordpro.dto.ApartmentDto;
import com.landlordpro.mapper.ApartmentMapper;
import com.landlordpro.repository.ApartmentRepository;
import com.landlordpro.repository.ExpenseRepository;
import com.landlordpro.repository.IncomeRepository;
import com.landlordpro.repository.TenantRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ApartmentService {
    private final ApartmentRepository apartmentRepository;

    private final ApartmentMapper apartmentMapper;
    private final IncomeRepository incomeRepository;
    private final TenantRepository tenantRepository;
    private final ExpenseRepository expenseRepository;

    public ApartmentService(
        ApartmentMapper apartmentMapper,
        ApartmentRepository apartmentRepository,
        IncomeRepository incomeRepository,
        TenantRepository tenantRepository,
        ExpenseRepository expenseRepository) {
        this.apartmentMapper = apartmentMapper;
        this.apartmentRepository = apartmentRepository;
        this.incomeRepository = incomeRepository;
        this.tenantRepository = tenantRepository;
        this.expenseRepository = expenseRepository;
    }

    public boolean isExistsForUser(UUID apartmentID, UUID userId) {
        return apartmentRepository.existsByIdAndUserId(apartmentID, userId);
    }

    public List<String> getApartmentNamesForUser(UUID userId) {
        return apartmentRepository.findApartmentNamesByUserId(userId);
    }

    public Map<UUID, String> getApartmentIdNameMap(UUID userId) {
        List<Apartment> apartments = apartmentRepository.findByUserId(userId);
        return apartments.stream()
            .collect(Collectors.toMap(
                Apartment::getId,      // Key: Apartment ID
                Apartment::getApartmentShortName // Value: Apartment Name
            ));
    }

    public Map<UUID, String> getApartmentIdNameMap(List<UUID> apartmentIds) {
        List<Apartment> apartments = apartmentRepository.findByIdIn(apartmentIds);

        // Transforming the result into a Map
        Map<UUID, String> apartmentIdNameMap = new HashMap<>();
        for (Apartment apartment : apartments) {
            apartmentIdNameMap.put(apartment.getId(), apartment.getApartmentShortName());
        }

        return apartmentIdNameMap;
    }

    public List<ApartmentDto> getApartmentsForUser(UUID userId) {
        return apartmentMapper.toDTOList(apartmentRepository.findByUserId(userId));
    }

    public ApartmentDto getApartment(UUID id) {
        // Assuming you have a method to fetch the apartment by ID
        Apartment apartment = apartmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Apartment not found"));
        return apartmentMapper.toDTO(apartment);
    }

    public void update(ApartmentDto apartmentDto, UUID userId) {
        if (!isExistsForUser(apartmentDto.getId(), userId)) {
            String errorMsg = "Apartment= " + apartmentDto.getApartmentShortName() + " not exists for the logged-in user.";
            throw new RuntimeException(errorMsg);
        }
        save(apartmentDto, apartmentDto.getId());
    }

    public void add(ApartmentDto apartmentDto) {
        save(apartmentDto, null);
    }

    private void save(ApartmentDto apartmentDto, UUID existingApartmentId) {
        // Modify the uniqueness check to ignore the current apartment being updated
        if (isUniueApartmentNameForUser(apartmentDto.getApartmentShortName(), apartmentDto.getUserId(), existingApartmentId)) {
            String errorMsg = "Apartment= " + apartmentDto.getApartmentShortName() + " already exists for the logged-in user.";
            throw new RuntimeException(errorMsg);
        }
        try {
            apartmentRepository.save(apartmentMapper.toEntity(apartmentDto));
        } catch (DataIntegrityViolationException ex) {
            String errorMessage = "Constraint violation while saving apartment=" + apartmentDto.getId() + " User=" + apartmentDto.getUserId();
            log.error(errorMessage, ex); // Assuming you have a logger in place
            throw new RuntimeException(errorMessage, ex);
        } catch (Exception ex) {
            String errorMessage = "Unexpected error while saving apartment=" + apartmentDto.getId() + " User=" + apartmentDto.getUserId();
            log.error(errorMessage, ex); // Assuming you have a logger in place
            throw new RuntimeException(errorMessage, ex);
        }
    }

    private boolean isUniueApartmentNameForUser(String apartmentShortName, UUID userId, UUID existingApartmentId) {
        // Check if there's another apartment with the same name for the user, but not the same apartment ID (when updating)
        return apartmentRepository.findByApartmentShortNameIgnoreCaseAndUserId(apartmentShortName.toLowerCase(), userId)
            .filter(apartment -> !apartment.getId().equals(existingApartmentId))
            .isPresent();
    }

    @Transactional
    public void delete(UUID id, UUID userId) {
        // Check if the apartment exists and belongs to the user
        apartmentRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new IllegalArgumentException("Apartment not found or does not belong to the user"));

        // Validate dependencies
        boolean hasIncome = incomeRepository.existsByApartmentId(id);
        boolean hasTenant = tenantRepository.existsByApartmentId(id);
        boolean hasExpense = expenseRepository.existsByApartmentId(id);

        if (hasIncome || hasTenant || hasExpense) {
            throw new IllegalStateException("Cannot delete apartment with existing income, tenants, or expenses.");
        }

        // Perform the delete operation
        apartmentRepository.deleteByIdAndUserId(id, userId);
    }

}
