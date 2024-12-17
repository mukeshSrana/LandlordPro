package com.landlordpro.service;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.landlordpro.model.Apartment;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ApartmentService {
    private final ObjectMapper objectMapper;

    @Value("${app.file-storage.apartment}")
    private String fileStorageApartmentDir;

    public ApartmentService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public boolean isExists(Apartment apartment) {
        Path directoryPath = Paths.get(fileStorageApartmentDir);
        Path filePath = directoryPath.resolve(apartment.getApartmentName() + ".json");
        return Files.exists(filePath); // Simplified: Returns true if file exists
    }

    public void save(Apartment apartment) throws IOException {
        log.info("Saving apartment: {}, year: {}", apartment.getApartmentName());

        Path directoryPath = Paths.get(fileStorageApartmentDir);
        Path filePath = directoryPath.resolve(apartment.getApartmentName() + ".json");

        try {
            // Ensure the directory exists
            Files.createDirectories(directoryPath);

            if (isExists(apartment)) {
                throw new FileAlreadyExistsException("File already exists: " + filePath);
            }

            // Serialize the apartment to JSON
            String apartmentJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(apartment);

            // Write the apartment JSON to the file
            Files.writeString(filePath, apartmentJson, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            log.info("Apartment: {} successfully saved in file: {}", apartment.getApartmentName(), filePath);

        } catch (IOException e) {
            log.error("Error saving apartment: {}", apartment.getApartmentName(), e);
            throw e; // Re-throw to be handled by controller
        }
    }
}
