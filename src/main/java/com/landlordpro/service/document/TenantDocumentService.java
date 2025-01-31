package com.landlordpro.service.document;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.landlordpro.dto.DocumentDto;
import com.landlordpro.dto.TenantDto;
import com.landlordpro.service.TenantService;

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
    public DocumentDto findById(UUID id, String documentType) {
        TenantDto tenant = tenantService.findById(id);
        byte[] documentData;
        String filename;

        switch (documentType.toLowerCase()) {
        case "privacy-policy":
            documentData = tenant.getPrivatePolicy();
            filename = tenant.getFullName() + "_privacy_policy.pdf";
            break;
        case "contract":
            documentData = tenant.getReceiptData();
            filename = tenant.getFullName() + "rental_contract.pdf";
            break;
        default:
            throw new IllegalArgumentException("Unsupported document type: " + documentType);
        }

        return new DocumentDto(documentData, filename);
    }

    @Override
    public String getFilename(DocumentDto documentDto) {
        return documentDto.getFilename();
    }
}

