package com.landlordpro.service;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.landlordpro.model.Apartment;
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

    public List<String> apartmentNames() {
        try {
            Path directoryPath = Paths.get(fileStorageTenantDir);
            return Files.list(directoryPath)
                .filter(file -> file.toString().endsWith(".json"))
                .map(file -> file.getFileName().toString().replaceFirst("\\.json$", ""))
                .collect(Collectors.toList());
        } catch (IOException e) {
            // Log the exception and return an empty list
            System.err.println("Error reading apartment files: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Tenant> tenants() {
        Path directoryPath = Paths.get(fileStorageTenantDir);
        try {
            // List all JSON files in the directory
            return Files.list(directoryPath)
                .filter(file -> file.toString().endsWith(".json")) // Only consider .json files
                .map(file -> {
                    try {
                        // Read the file and deserialize the content into an Tenant object
                        return objectMapper.readValue(file.toFile(), Tenant.class);
                    } catch (IOException e) {
                        log.error("Error reading file: {}", file, e);
                        return null; // You can choose to skip files that cause errors or handle them differently
                    }
                })
                .filter(tenant -> tenant != null) // Filter out any null apartments (if any deserialization fails)
                .collect(Collectors.toList()); // Collect results into a list
        } catch (IOException e) {
            log.error("Error reading tenants from directory: {}", directoryPath, e);
            return Collections.emptyList(); // Return an empty list if an error occurs
        }
    }

    public boolean isExists(Tenant tenant) {
        Path directoryPath = Paths.get(fileStorageTenantDir);
        Path filePath = directoryPath.resolve(tenant.getFullName() + ".json");
        return Files.exists(filePath); // Simplified: Returns true if file exists
    }

    public void save(Tenant tenant) throws IOException {
        log.info("Saving tenant: {}", tenant.getFullName());

        Path directoryPath = Paths.get(fileStorageTenantDir);
        Path filePath = directoryPath.resolve(tenant.getFullName() + ".json");

        try {
            // Ensure the directory exists
            Files.createDirectories(directoryPath);

            if (isExists(tenant)) {
                throw new FileAlreadyExistsException("File already exists: " + filePath);
            }

            // Serialize the apartment to JSON
            String apartmentJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tenant);

            // Write the apartment JSON to the file
            Files.writeString(filePath, apartmentJson, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            log.info("Apartment: {} successfully saved in file: {}", tenant.getFullName(), filePath);

        } catch (IOException e) {
            log.error("Error saving apartment: {}", tenant.getFullName(), e);
            throw e; // Re-throw to be handled by controller
        }
    }
}
