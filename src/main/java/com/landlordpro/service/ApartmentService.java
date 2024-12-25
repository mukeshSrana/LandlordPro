package com.landlordpro.service;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.landlordpro.domain.Apartment;
import com.landlordpro.dto.ApartmentDto;
import com.landlordpro.mapper.ApartmentMapper;
import com.landlordpro.repository.ApartmentRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ApartmentService {
    private final ApartmentRepository apartmentRepository;

    private final ApartmentMapper apartmentMapper;

    public ApartmentService(ApartmentMapper apartmentMapper, ApartmentRepository apartmentRepository) {
        this.apartmentMapper = apartmentMapper;
        this.apartmentRepository = apartmentRepository;
    }

    public boolean isExistsForUser(String apartmentName, UUID userId) {
        return apartmentRepository.existsByApartmentShortNameAndUserId(apartmentName, userId);
    }

    public List<String> getApartmentNamesForUser(UUID userId) {
        return apartmentRepository.findApartmentNamesByUserId(userId);
    }

    public List<ApartmentDto> getApartmentsForUser(UUID userId) {
        return apartmentMapper.toDTOList(apartmentRepository.findByUserId(userId));
    }

    public ApartmentDto getApartment(UUID id) {
        // Assuming you have a method to fetch the apartment by ID
        Apartment apartment = apartmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Apartment not found"));
        return apartmentMapper.toDTO(apartment);
    }

    public void save(ApartmentDto apartmentDto) {
        Apartment apartment = apartmentMapper.toEntity(apartmentDto);
        try {
            apartmentRepository.save(apartment);
        } catch (DataIntegrityViolationException ex) {
            String errorMessage = "Constraint violation while saving apartment=" + apartmentDto.getId() + "User=" + apartmentDto.getUserId();
            log.error(errorMessage, ex); // Assuming you have a logger in place
            throw new RuntimeException(errorMessage, ex);
        } catch (Exception ex) {
            String errorMessage = "Unexpected error while saving apartment=" + apartmentDto.getId() + "User=" + apartmentDto.getUserId();
            log.error(errorMessage, ex); // Assuming you have a logger in place
            throw new RuntimeException(errorMessage, ex);
        }
    }

//    public void update(ApartmentDto apartmentDto) {
//        if (!isExistsForUser(apartmentDto.getApartmentShortName(), apartmentDto.getUserId())) {
//            throw new RuntimeException(
//                "Apartment with ID " + apartmentDto.getId() + " for User ID " + apartmentDto.getUserId() + " not found.");
//        }
//        save(apartmentDto);
//    }
}
