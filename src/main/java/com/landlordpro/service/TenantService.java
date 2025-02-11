package com.landlordpro.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.landlordpro.domain.Apartment;
import com.landlordpro.domain.Tenant;
import com.landlordpro.domain.User;
import com.landlordpro.dto.ApartmentDto;
import com.landlordpro.dto.TenantDto;
import com.landlordpro.dto.UserDto;
import com.landlordpro.mapper.ApartmentMapper;
import com.landlordpro.mapper.TenantMapper;
import com.landlordpro.mapper.UserMapper;
import com.landlordpro.repository.ApartmentRepository;
import com.landlordpro.repository.IncomeRepository;
import com.landlordpro.repository.TenantRepository;
import com.landlordpro.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class TenantService {
    private final TenantRepository tenantRepository;
    private final ApartmentRepository apartmentRepository;
    private final UserRepository userRepository;
    private final IncomeRepository incomeRepository;
    private final TenantMapper tenantMapper;
    private final UserMapper userMapper;
    private final ApartmentMapper apartmentMapper;

    public TenantService(
        TenantRepository tenantRepository, ApartmentRepository apartmentRepository,
        UserRepository userRepository, IncomeRepository incomeRepository,
        TenantMapper tenantMapper,
        UserMapper userMapper, ApartmentMapper apartmentMapper) {
        this.tenantRepository = tenantRepository;
        this.apartmentRepository = apartmentRepository;
        this.userRepository = userRepository;
        this.incomeRepository = incomeRepository;
        this.tenantMapper = tenantMapper;
        this.userMapper = userMapper;
        this.apartmentMapper = apartmentMapper;
    }

    public void add(TenantDto tenantDto) {
        try {
            Tenant tenant = tenantMapper.toEntity(tenantDto);
            validate(tenant);
            save(tenant);
            //emailService.sendPrivacyPolicyEmail(privatePolicy, tenant.getEmail(), tenant.getFullName(), user.getName());
        } catch (IllegalStateException e) {
            throw new RuntimeException("Validation failed: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while adding tenant", e);
        }
    }

    private void save(Tenant tenant) {
        try {
            tenantRepository.save(tenant);
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Tenant already exists or constraint violation for tenant=" + tenant.getFullName(), ex);
        } catch (Exception ex) {
            throw new RuntimeException("Unexpected error while saving tenant=" + tenant.getFullName(), ex);
        }
    }

    private void validate(Tenant tenant) {
        List<Tenant> tenants = tenantRepository.findByUserIdAndApartmentId(tenant.getUserId(), tenant.getApartmentId());

        List<Tenant> activeTenants = tenants.stream()
            .filter(t -> t.getLeaseEndDate() == null || t.getLeaseEndDate().isAfter(LocalDate.now()))
            .collect(Collectors.toList());

        if (activeTenants.contains(tenant)) {
            throw new IllegalStateException(
                "Tenant " + tenant.getFullName()
                    + " is already active for apartment "
                    + getApartment(tenant.getApartmentId(), tenant.getUserId()).getApartmentShortName());
        }
    }

    public void update(TenantDto tenantDto, UUID userId) throws RuntimeException {
        if (!isExistsForUser(tenantDto.getId(), userId, tenantDto.getApartmentId())) {
            String errorMsg = "Tenant= " + tenantDto.getFullName() + " not exists for the logged-in user.";
            throw new RuntimeException(errorMsg);
        }

        save(tenantDto);
    }

    private void save(TenantDto tenantDto) throws RuntimeException {
        Tenant tenant = tenantMapper.toEntity(tenantDto);
        try {
            tenantRepository.save(tenant);
        } catch (DataIntegrityViolationException ex) {
            String errorMessage = "Constraint violation while saving tenant=" + tenant.getFullName() + " User=" + tenant.getUserId();
            throw new RuntimeException(errorMessage, ex);
        } catch (Exception ex) {
            String errorMessage = "Unexpected error while saving tenant=" + tenant.getFullName() + " User=" + tenant.getUserId();
            throw new RuntimeException(errorMessage, ex);
        }
    }

    public boolean isExistsForUser(UUID id, UUID userId, UUID apartmentId) {
        return tenantRepository.existsByIdAndUserIdAndApartmentId(id, userId, apartmentId);
    }

    @Transactional
    public void deleteTenant(UUID tenantId, UUID userId, UUID apartmentId) {
        // Check if the tenant exists
        Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new IllegalArgumentException("Tenant not found."));

        // Check if the tenant belongs to the specified user and apartment
        if (!tenant.getUserId().equals(userId) || !tenant.getApartmentId().equals(apartmentId)) {
            throw new IllegalStateException("Tenant does not belong to the given user or apartment.");
        }

        // Check if the tenant has an active lease
        if (tenant.getLeaseEndDate() == null || tenant.getLeaseEndDate().isAfter(LocalDate.now())) {
            throw new IllegalStateException(
                "Cannot delete tenant " + tenant.getFullName() + " as they have an active lease for apartment " +
                    getApartment(apartmentId, userId).getApartmentShortName());
        }

        // Check if there are income records associated with the tenant
        if (incomeRepository.countByTenantId(tenantId) > 0) {
            throw new IllegalStateException(
                "Cannot delete tenant " + tenant.getFullName() + " as income records exist for apartment " +
                    getApartment(apartmentId, userId).getApartmentShortName());
        }

        // Proceed with deletion
        tenantRepository.deleteById(tenantId);
    }

    private ApartmentDto getApartment(UUID apartmentId, UUID userId) {
        Apartment apartment = apartmentRepository.findByIdAndUserId(apartmentId, userId)
            .orElseThrow(() -> new RuntimeException("Apartment not found for user-id " + userId));
        return apartmentMapper.toDTO(apartment);
    }

    public  UserDto getUser(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User (landlord) not found for user-id " + userId));
        return  userMapper.toDTO(user);
    }

    public Map<UUID, String> getTenantIdNameMap(UUID userId) {
        List<Tenant> tenants = tenantRepository.findByUserId(userId);
        return tenants.stream()
            .collect(Collectors.toMap(
                Tenant::getId,
                Tenant::getFullName
            ));
    }

    public Map<UUID, String> getTenantIdNameMap(List<UUID> apartmentIds) {
        List<Tenant> tenants = tenantRepository.findByIdIn(apartmentIds);

        // Transforming the result into a Map
        Map<UUID, String> tenantIdNameMap = new HashMap<>();
        for (Tenant tenant : tenants) {
            tenantIdNameMap.put(tenant.getId(), tenant.getFullName());
        }

        return tenantIdNameMap;
    }

    public List<Map<String, String>> findActiveTenantsByUserIdAndApartmentId(UUID userId, UUID apartmentId) {
        return tenantRepository.findActiveTenantsByUserIdAndApartmentId(userId, apartmentId)
            .stream()
            .map(tenant -> Map.of("id", tenant.getId().toString(), "name", tenant.getFullName()))
            .toList();
    }

    public List<TenantDto> getTenantsForUser(UUID userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");

        List<Tenant> tenants = tenantRepository.findByUserId(userId);
        return tenants.isEmpty() ? Collections.emptyList() : tenantMapper.toDTOList(tenants);
    }


    public TenantDto findById(UUID id) {
        if (Objects.isNull(id)) {
            return null;
        }

        return tenantRepository.findById(id)
            .map(tenantMapper::toDTO) // Convert entity to DTO if present
            .orElseThrow(() -> new EntityNotFoundException("Tenant not found with ID: " + id));
    }
}

