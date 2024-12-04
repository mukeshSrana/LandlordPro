package com.landlordpro.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.landlordpro.model.Expense;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExpenseService {
    private final Path basePath = Paths.get("./data/expenses");
    private final ObjectMapper objectMapper;

    @Value("${app.file-storage.base-dir}")
    private String fileStorageBaseDir;

//    @Value("${app.file-storage.filenames}")
//    private List<String> filenames;

    public ExpenseService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void saveExpense(Expense expense) throws IOException {
        log.info("Saving expense for apartment: {}, year: {}", expense.getApartmentName(), expense.getDate().getYear());

        Path directoryPath = Paths.get(fileStorageBaseDir, String.valueOf(expense.getDate().getYear()));
        Path filePath = directoryPath.resolve(expense.getApartmentName() + ".json");

        try {
            // Ensure the directory exists
            Files.createDirectories(directoryPath);

            // Read existing data or initialize a new list
            List<Expense> expenses;
            if (Files.exists(filePath)) {
                String existingJson = Files.readString(filePath);
                expenses = objectMapper.readValue(existingJson, new TypeReference<>() { });
            } else {
                expenses = new ArrayList<>();
            }

            // Add the new expense
            expenses.add(expense);

            // Write the updated list back to the file
            String updatedJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(expenses);
            Files.writeString(filePath, updatedJson, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            log.info("Expense saved to file: {}", filePath);

        } catch (IOException e) {
            log.error("Error saving expense for apartment: {}", expense.getApartmentName(), e);
            throw e;
        }
    }

    public List<Expense> getAllExpenses() throws IOException {
        if (Files.notExists(basePath)) return Collections.emptyList();
        return Files.list(basePath)
            .filter(Files::isRegularFile)
            .map(path -> {
                try {
                    return objectMapper.readValue(path.toFile(), Expense.class);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            })
            .collect(Collectors.toList());
    }

    public Optional<Expense> getExpenseById(String id) throws IOException {
        if (Files.notExists(basePath)) return Optional.empty();
        return Files.list(basePath)
            .filter(path -> path.getFileName().toString().startsWith(id))
            .map(path -> {
                try {
                    return objectMapper.readValue(path.toFile(), Expense.class);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            })
            .findFirst();
    }

    public void updateExpense(String id, Expense expense) throws IOException {
        deleteExpense(id); // First delete the old expense
        saveExpense(expense); // Then create a new one with the updated details
    }

    public boolean deleteExpense(String id) throws IOException {
        if (Files.notExists(basePath)) return false;
        Optional<Path> fileToDelete = Files.list(basePath)
            .filter(path -> path.getFileName().toString().startsWith(id))
            .findFirst();
        if (fileToDelete.isPresent()) {
            Files.delete(fileToDelete.get());
            return true;
        }
        return false;
    }
}
