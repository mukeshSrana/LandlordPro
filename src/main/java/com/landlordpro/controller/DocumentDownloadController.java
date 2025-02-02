package com.landlordpro.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.tika.Tika;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.landlordpro.dto.DocumentDto;
import com.landlordpro.service.document.DocumentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/download")
@PreAuthorize("hasRole('ROLE_LANDLORD')")
public class DocumentDownloadController {

    private final Map<String, DocumentService> documentServices;
    private final Tika tika = new Tika();

    public DocumentDownloadController(List<DocumentService> documentServices) {
        this.documentServices = documentServices.stream()
            .collect(Collectors.toMap(DocumentService::getType, Function.identity()));
    }

    @GetMapping("/{type}/{id}/{documentType}")
    public ResponseEntity<byte[]> downloadDocument(
        @PathVariable String type,
        @PathVariable UUID id,
        @PathVariable String documentType,
        Model model) {
        try {
            DocumentService service = documentServices.get(type.toLowerCase());
            if (service == null) {
                return ResponseEntity.badRequest().body(("Invalid document type: " + type).getBytes());
            }

            DocumentDto documentDto = service.findById(id, documentType);
            if (documentDto.getData() == null) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileData = documentDto.getData();
            String mimeType = tika.detect(fileData);
            mimeType = (mimeType != null) ? mimeType : "application/octet-stream";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(mimeType));
            headers.setContentDisposition(ContentDisposition.inline()
                .filename(service.getFilename(documentDto))
                .build());

            return new ResponseEntity<>(fileData, headers, HttpStatus.OK);

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Unexpected error occurred: " + e.getMessage());
            log.error("Error downloading document for type {}: {}", type, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

