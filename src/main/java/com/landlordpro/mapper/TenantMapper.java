package com.landlordpro.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.landlordpro.domain.Tenant;
import com.landlordpro.dto.TenantDto;

@Mapper(componentModel = "spring")
public interface TenantMapper {

    // Mapping a single Tenant entity to TenantDto
    TenantDto toDTO(Tenant tenant);

    // Mapping a single TenantDto to Tenant entity
    Tenant toEntity(TenantDto tenantDto);

    // Mapping a list of Tenant entities to a list of TenantDto
    List<TenantDto> toDTOList(List<Tenant> tenants);

    // Mapping a list of TenantDto to a list of Tenant entities
    List<Tenant> toEntityList(List<TenantDto> tenantDtos);
}

