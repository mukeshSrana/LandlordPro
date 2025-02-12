package com.landlordpro.service.document;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.landlordpro.dto.DocumentDto;
import com.landlordpro.dto.IncomeDto;
import com.landlordpro.service.IncomeService;

@Service
public class IncomeDocumentService implements DocumentService {

    private final IncomeService incomeService;

    public IncomeDocumentService(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @Override
    public String getType() {
        return "income";
    }

    @Override
    public DocumentDto findById(UUID id, String documentType) {
        if (documentType.equalsIgnoreCase("receipt")) {
            IncomeDto income = incomeService.findById(id);
            return new DocumentDto(income.getReceiptData(), "RentalReceipt.pdf");
        }
        throw new IllegalArgumentException("Unsupported document type: " + documentType);
    }

    @Override
    public String getFilename(DocumentDto documentDto) {
        return documentDto.getFilename();
    }
}

