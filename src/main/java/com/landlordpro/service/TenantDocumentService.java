package com.landlordpro.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.landlordpro.dto.DocumentDto;
import com.landlordpro.dto.TenantDto;

@Service
public class TenantDocumentService implements DocumentService {

    private final TenantService tenantService;

    public TenantDocumentService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @Override
    public String getType() {
        return "tenant";
    }

    @Override
    public DocumentDto findById(UUID id) {
        TenantDto tenant = tenantService.findById(id);
        return new DocumentDto(tenant.getPrivatePolicy(), tenant.getFullName() + "-PrivatePolicy.pdf");
    }

    @Override
    public String getFilename(DocumentDto documentDto) {
        return documentDto.getFilename();
    }
}

