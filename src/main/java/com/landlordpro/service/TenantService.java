package com.landlordpro.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.landlordpro.exception.DuplicateRecordException;
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

            // Read existing expenses or initialize an empty list if the file doesn't exist
            List<Tenant> tenants = new ArrayList<>();
            if (Files.exists(filePath)) {
                String existingJson = Files.readString(filePath);
                // Deserialize existing JSON content into a list of expenses
                tenants = objectMapper.readValue(existingJson, new TypeReference<>() { });
            }

            // Check if the tenant already exists
            boolean tenantExists = tenants.stream().anyMatch(existingTenant -> existingTenant.equals(tenant));
            if (tenantExists) {
                log.warn("Tenant: {} already exists in file: {}", tenant.getFullName(), filePath);
                throw new DuplicateRecordException("Tenant already exists: " + tenant.getFullName());
            }

            // Add the new expense to the list
            tenants.add(tenant);

            // Serialize the apartment to JSON
            String updatedJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tenants);

            // Write the apartment JSON to the file
            Files.writeString(filePath, updatedJson, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            log.info("Tenant: {} successfully saved in file: {}", tenant.getFullName(), filePath);

        } catch (IOException e) {
            log.error("Error saving tenant: {}", tenant.getFullName(), e);
            throw e; // Re-throw to be handled by controller
        }
    }
}
