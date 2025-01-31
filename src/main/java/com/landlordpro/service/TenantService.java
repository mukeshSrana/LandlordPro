package com.landlordpro.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.landlordpro.domain.Apartment;
import com.landlordpro.domain.Tenant;
import com.landlordpro.domain.User;
import com.landlordpro.dto.TenantDto;
import com.landlordpro.mapper.TenantMapper;
import com.landlordpro.repository.ApartmentRepository;
import com.landlordpro.repository.TenantRepository;
import com.landlordpro.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TenantService {
    private final TenantRepository tenantRepository;
    private final ApartmentRepository apartmentRepository;
    private final UserRepository userRepository;
    private final TenantMapper tenantMapper;
    private final EmailService emailService;

    public TenantService(
        TenantRepository tenantRepository, ApartmentRepository apartmentRepository,
        UserRepository userRepository,
        TenantMapper tenantMapper,
        EmailService emailService) {
        this.tenantRepository = tenantRepository;
        this.apartmentRepository = apartmentRepository;
        this.userRepository = userRepository;
        this.tenantMapper = tenantMapper;
        this.emailService = emailService;
    }

    public void add(TenantDto tenantDto) {
        try {
            Tenant tenant = tenantMapper.toEntity(tenantDto);
            validate(tenant);
            User user = getUser(tenant);
            byte[] privatePolicy = emailService.generatePrivatePolicyPdf(user.getName(), user.getUsername(), user.getMobileNumber());
            tenant.setPrivatePolicy(privatePolicy);
            save(tenant);
            emailService.sendPrivacyPolicyEmail(privatePolicy, tenant.getEmail(), tenant.getFullName(), user.getName());
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
                "Tenant " + tenant.getFullName() + " is already active for apartment " + getApartment(tenant).getApartmentShortName());
        }
    }

    private Apartment getApartment(Tenant tenant) {
        return apartmentRepository.findByIdAndUserId(tenant.getApartmentId(), tenant.getUserId())
            .orElseThrow(() -> new RuntimeException("Apartment not found for user-id " + tenant.getUserId()));
    }

    private User getUser(Tenant tenant) {
        return userRepository.findById(tenant.getUserId())
            .orElseThrow(() -> new RuntimeException("User (landlord) not found for user-id " + tenant.getUserId()));
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
        try {
            // Validate the input
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }

            // Fetch expenses
            List<Tenant> tenants = tenantRepository.findByUserId(userId);
            return tenantMapper.toDTOList(tenants);
        } catch (EmptyResultDataAccessException ex) {
            // Handle specific database "no data found" case
            throw new RuntimeException("No tenants found for user with ID: " + userId, ex);

        } catch (IllegalArgumentException ex) {
            // Handle invalid input
            throw ex; // Rethrow, or handle as needed

        } catch (RuntimeException ex) {
            // Catch unexpected runtime exceptions
            throw new RuntimeException("Unexpected error occurred", ex);
        }
    }

}

