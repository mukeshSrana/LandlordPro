package com.landlordpro.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.landlordpro.domain.Tenant;
import com.landlordpro.dto.TenantDto;
import com.landlordpro.mapper.TenantMapper;
import com.landlordpro.repository.TenantRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TenantService {
    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;

    public TenantService(TenantRepository tenantRepository, TenantMapper tenantMapper) {
        this.tenantRepository = tenantRepository;
        this.tenantMapper = tenantMapper;
    }

    public void add(TenantDto tenantDto) throws Exception {
        save(tenantDto);
    }

    private void save(TenantDto tenantDto) throws RuntimeException {
        Tenant tenant = tenantMapper.toEntity(tenantDto);

        try {
            validate(tenant);
            tenantRepository.save(tenant);
        } catch (DataIntegrityViolationException ex) {
            String errorMessage = "Constraint violation while saving tenant=" + tenantDto.getFullName();
            log.error(errorMessage, ex); // Assuming you have a logger in place
            throw new RuntimeException(errorMessage, ex);
        } catch (Exception ex) {
            String errorMessage = "Unexpected error while saving tenant=" + tenantDto.getFullName();
            log.error(errorMessage, ex); // Assuming you have a logger in place
            throw new RuntimeException(errorMessage, ex);
        }
    }

    private void validate(Tenant tenant) {
        List<Tenant> tenants =
            tenantRepository.findByUserIdAndApartmentId(tenant.getUserId(), tenant.getApartmentId());
        List<Tenant> activeTenants = tenants.stream()
            .filter(t -> t.getLeaseEndDate() == null || t.getLeaseEndDate().isAfter(LocalDate.now()))
            .collect(Collectors.toList());

        boolean validate = !activeTenants.contains(tenant);

        if (!validate) {
            throw new IllegalStateException("Tenant is already active.");
        }
    }

    public Map<UUID, String> getTenantIdNameMap(UUID userId) {
        List<Tenant> tenants = tenantRepository.findByUserId(userId);
        return tenants.stream()
            .collect(Collectors.toMap(
                Tenant::getId,
                Tenant::getFullName
            ));
    }

    public List<Map<String, String>> getTenantsByApartmentId(UUID userId, UUID apartmentId) {
        return tenantRepository.findByUserIdAndApartmentId(userId, apartmentId)
            .stream()
            .map(tenant -> Map.of("id", tenant.getId().toString(), "name", tenant.getFullName()))
            .toList();
    }
}

