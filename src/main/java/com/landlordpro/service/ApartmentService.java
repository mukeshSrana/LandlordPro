package com.landlordpro.service;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<String> apartmentNames() {
        try {
            Path directoryPath = Paths.get(fileStorageApartmentDir);
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

    public Map<String, String> apartmentNamesWithId() {
        Path directoryPath = Paths.get(fileStorageApartmentDir);
        try {
            // List all JSON files in the directory and map them to a HashMap
            return Files.list(directoryPath)
                .filter(file -> file.toString().endsWith(".json")) // Only consider .json files
                .map(file -> {
                    try {
                        // Read the file and deserialize the content into an Apartment object
                        Apartment apartment = objectMapper.readValue(file.toFile(), Apartment.class);
                        // Return a Map entry with apartmentId as key and apartmentName as value
                        return Map.entry(apartment.getId(), apartment.getApartmentName());
                    } catch (IOException e) {
                        log.error("Error reading file: {}", file, e);
                        return null; // Skip invalid files
                    }
                })
                .filter(entry -> entry != null) // Filter out any null entries (if any deserialization fails)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)); // Collect into a HashMap
        } catch (IOException e) {
            log.error("Error reading apartments from directory: {}", directoryPath, e);
            return Collections.emptyMap(); // Return an empty map if an error occurs
        }
    }

    public List<Apartment> apartments() {
        Path directoryPath = Paths.get(fileStorageApartmentDir);
        try {
            // List all JSON files in the directory
            return Files.list(directoryPath)
                .filter(file -> file.toString().endsWith(".json")) // Only consider .json files
                .map(file -> {
                    try {
                        // Read the file and deserialize the content into an Apartment object
                        return objectMapper.readValue(file.toFile(), Apartment.class);
                    } catch (IOException e) {
                        log.error("Error reading file: {}", file, e);
                        return null; // You can choose to skip files that cause errors or handle them differently
                    }
                })
                .filter(apartment -> apartment != null) // Filter out any null apartments (if any deserialization fails)
                .collect(Collectors.toList()); // Collect results into a list
        } catch (IOException e) {
            log.error("Error reading apartments from directory: {}", directoryPath, e);
            return Collections.emptyList(); // Return an empty list if an error occurs
        }
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
