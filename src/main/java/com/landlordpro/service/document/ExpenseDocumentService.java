package com.landlordpro.service.document;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.landlordpro.dto.DocumentDto;
import com.landlordpro.dto.ExpenseDto;
import com.landlordpro.service.ExpenseService;

@Service
public class ExpenseDocumentService implements DocumentService {

    private final ExpenseService expenseService;

    public ExpenseDocumentService(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @Override
    public String getType() {
        return "expense";
    }

    @Override
    public DocumentDto findById(UUID id, String documentType) {
        ExpenseDto expense = expenseService.findById(id);
        return new DocumentDto(expense.getReceiptData(), expense.getName() + ".pdf");
    }

    @Override
    public String getFilename(DocumentDto documentDto) {
        return documentDto.getFilename();
    }
}

