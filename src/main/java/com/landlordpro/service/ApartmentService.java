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
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;
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
        return processDirectory(
            fileStorageApartmentDir,
            file -> {
                try {
                    return objectMapper.readValue(file.toFile(), Apartment.class).getApartmentName();
                } catch (IOException e) {
                    log.error("Error reading file: {}", file, e);
                    return null;
                }
            },
            Collectors.toList(),
            Collections.emptyList()
        );
    }

    private <T, R> R processDirectory(
        String directoryPathStr,
        Function<Path, T> fileProcessor,
        Collector<T, ?, R> collector,
        R defaultValue
    ) {
        Path directoryPath = Paths.get(directoryPathStr);
        try {
            if (!Files.exists(directoryPath) || !Files.isDirectory(directoryPath)) {
                log.warn("Directory does not exist or is not a directory(): {}", directoryPath);
                return defaultValue;
            }

            return Files.list(directoryPath)
                .filter(file -> file.toString().endsWith(".json"))
                .map(fileProcessor)
                .filter(Objects::nonNull)
                .collect(collector);
        } catch (IOException e) {
            log.error("Error processing directory: {}", directoryPath, e);
            return defaultValue;
        }
    }

    public Map<String, String> apartmentNamesWithId() {
        return processDirectory(
            fileStorageApartmentDir,
            file -> {
                try {
                    Apartment apartment = objectMapper.readValue(file.toFile(), Apartment.class);
                    return Map.entry(apartment.getId(), apartment.getApartmentName());
                } catch (IOException e) {
                    log.error("Error reading file: {}", file, e);
                    return null;
                }
            },
            Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue),
            Collections.emptyMap()
        );
    }

    public List<Apartment> apartments() {
        return processDirectory(
            fileStorageApartmentDir,
            file -> {
                try {
                    return objectMapper.readValue(file.toFile(), Apartment.class);
                } catch (IOException e) {
                    log.error("Error reading file: {}", file, e);
                    return null;
                }
            },
            Collectors.toList(),
            Collections.emptyList()
        );
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
            if (isExists(apartment)) {
                throw new FileAlreadyExistsException("File already exists: " + filePath);
            }

            // Ensure the directory exists
            Files.createDirectories(directoryPath);

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
