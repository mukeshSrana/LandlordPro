package com.landlordpro.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.landlordpro.model.Tenant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TenantService {
    private final ObjectMapper objectMapper;

    @Value("${app.file-storage.tenant}")
    private String fileStorageTenantDir;

    public TenantService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void save(Tenant tenant) throws IOException {
        log.info("Saving tenant: {}", tenant.getFullName());

        Path directoryPath = Paths.get(fileStorageTenantDir);
        Path filePath = directoryPath.resolve(tenant.getApartmentName() + ".json");

        try {
            // Ensure the directory exists
            Files.createDirectories(directoryPath);

            // Serialize the apartment to JSON
            String tenantJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tenant);

            // Write the apartment JSON to the file
            Files.writeString(filePath, tenantJson, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            log.info("Tenant: {} successfully saved in file: {}", tenant.getFullName(), filePath);

        } catch (IOException e) {
            log.error("Error saving tenant: {}", tenant.getFullName(), e);
            throw e; // Re-throw to be handled by controller
        }
    }
}
