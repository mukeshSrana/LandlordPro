package com.landlordpro.service;

import java.util.UUID;

import com.landlordpro.dto.DocumentDto;

public interface DocumentService {
    String getType();
    DocumentDto findById(UUID id, String documentType);
    String getFilename(DocumentDto documentDto);
}

