package com.landlordpro.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.landlordpro.dto.ExpenseDto;
import com.landlordpro.mapper.ExpenseMapper;
import com.landlordpro.model.Expense;
import com.landlordpro.repository.ExpenseRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExpenseService {
    private final Path basePath = Paths.get("./data/expenses");
    private final ObjectMapper objectMapper;

    @Value("${app.file-storage.base-dir}")
    private String fileStorageBaseDir;

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;

    public ExpenseService(ObjectMapper objectMapper, ExpenseRepository expenseRepository, ExpenseMapper expenseMapper) {
        this.objectMapper = objectMapper;
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
    }

    public void saveExpense(Expense expense) throws IOException {
        log.info("Saving expense for apartment: {}, year: {}", expense.getApartmentName(), expense.getDate().getYear());

        // Build directory and file paths
        Path directoryPath = Paths.get(fileStorageBaseDir, expense.getYear());
        Path filePath = directoryPath.resolve(expense.getApartmentName() + ".json");

        try {
            // Ensure the directory exists
            Files.createDirectories(directoryPath);

            // Read existing expenses or initialize an empty list if the file doesn't exist
            List<Expense> expenses = new ArrayList<>();
            if (Files.exists(filePath)) {
                String existingJson = Files.readString(filePath);
                // Deserialize existing JSON content into a list of expenses
                expenses = objectMapper.readValue(existingJson, new TypeReference<List<Expense>>() {});
            }

            // Add the new expense to the list
            expenses.add(expense);

            // Serialize the updated list back to JSON with pretty printing
            String updatedJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(expenses);

            // Write the updated JSON to the file (overwriting the old content)
            Files.writeString(filePath, updatedJson, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            log.info("Expense successfully saved for apartment: {} in file: {}", expense.getApartmentName(), filePath);

        } catch (IOException e) {
            log.error("Error saving expense for apartment: {}", expense.getApartmentName(), e);
            throw e; // Re-throw the exception after logging it
        }
    }

    // Fetch distinct years from all expenses
    public List<String> getAvailableYears() throws IOException {
        return getAllExpenses().stream()
            .map(Expense::getYear)
            .distinct()
            .sorted() // Optional: Sort the years in ascending order
            .collect(Collectors.toList());
    }

    // Fetch distinct apartment names from all expenses
    public List<String> getAvailableApartments() throws IOException {
        return getAllExpenses().stream()
            .map(Expense::getApartmentName)
            .distinct()
            .sorted() // Optional: Sort apartment names alphabetically
            .collect(Collectors.toList());
    }

    public List<Expense> getExpensesFiltered(String year, String apartmentName) throws IOException {
        return getAllExpenses().stream()
            .filter(expense -> year == null || year.isEmpty() || year.equals(expense.getYear()))
            .filter(expense -> apartmentName == null || apartmentName.isEmpty() || apartmentName.equals(expense.getApartmentName()))
            .collect(Collectors.toList());
    }

    public List<Expense> getAllExpenses() throws IOException {
        log.info("Fetching all expenses from base path: {}", fileStorageBaseDir);

        // Directory path where the expenses are stored (per year and apartment)
        Path expensesDirectory = Paths.get(fileStorageBaseDir);

        // Check if the base directory exists
        if (Files.notExists(expensesDirectory) || !Files.isDirectory(expensesDirectory)) {
            log.warn("Directory does not exist: {}", expensesDirectory);
            return Collections.emptyList();  // Return an empty list if the directory doesn't exist
        }

        // List to hold all expenses from all files
        List<Expense> allExpenses = new ArrayList<>();

        // Process each year directory in the base directory (e.g., 2024)
        try {
            Files.list(expensesDirectory)
                .filter(Files::isDirectory)  // Ensure we process only directories
                .forEach(yearDirectory -> {
                    try {
                        // For each year directory, get the list of apartment files
                        Files.list(yearDirectory)
                            .filter(Files::isRegularFile)  // Only process regular files
                            .filter(file -> file.toString().endsWith(".json"))  // Ensure it's a JSON file
                            .forEach(file -> {
                                try {
                                    // Read the content of the file
                                    String jsonContent = Files.readString(file);
                                    // Deserialize the content into a list of expenses
                                    List<Expense> expenses = objectMapper.readValue(jsonContent, new TypeReference<>() { });
                                    // Add these expenses to the allExpenses list
                                    allExpenses.addAll(expenses);
                                } catch (IOException e) {
                                    log.error("Error reading expense file: {}", file, e);
                                }
                            });
                    } catch (IOException e) {
                        log.error("Error processing year directory: {}", yearDirectory, e);
                    }
                });
        } catch (IOException e) {
            log.error("Error reading expenses directory: {}", expensesDirectory, e);
            throw e;  // Propagate the error if directory reading fails
        }

        // Return the combined list of all expenses
        log.info("Fetched {} total expenses.", allExpenses.size());
        return allExpenses;
    }

    public Optional<Expense> getExpenseById(String id, String year, String apartmentName) throws IOException {
        Path filePath = basePath.resolve(year).resolve(apartmentName + ".json");

        if (Files.notExists(filePath)) return Optional.empty();
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

    public boolean updateExpense(String id, String year, String apartmentName, String name, BigDecimal amount) {
        Path filePath = basePath.resolve(year).resolve(apartmentName + ".json");

        // Check if file exists
        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("File not found for the given year and apartment: " + filePath);
        }

        try {
            // Read the file content
            List<Expense> expenses = objectMapper.readValue(Files.readString(filePath), new TypeReference<>() {});

            // Find and update the expense
            Expense updatedExpense = expenses.stream()
                .filter(expense -> expense.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Expense with ID " + id + " not found"));

            updatedExpense.setName(name);
            updatedExpense.setAmount(amount);

            // Write the updated list back to the file
            String updatedContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(expenses);
            Files.writeString(filePath, updatedContent);

            return true;
        } catch (IOException e) {
            throw new RuntimeException("Error processing file: " + filePath, e);
        }
    }

    public boolean deleteExpense(String id, String year, String apartment) {
        // Construct the path to the specific JSON file
        Path filePath = basePath.resolve(year).resolve(apartment + ".json");
        Path yearFolderPath = basePath.resolve(year);  // Path to the 'year' folder

        if (Files.exists(filePath)) {
            try {
                // Read the content of the file
                String content = Files.readString(filePath);

                // Deserialize the file content into a list of expenses
                List<Expense> expenses = objectMapper.readValue(content, new TypeReference<List<Expense>>() { });

                // Remove the expense with the matching id
                boolean removed = expenses.removeIf(expense -> expense.getId().equals(id));

                if (removed) {
                    if (expenses.isEmpty()) {
                        // If the updated list is empty, delete the file
                        Files.delete(filePath);

                        // Check if the parent folder (year folder) is empty
                        try (Stream<Path> paths = Files.list(yearFolderPath)) {
                            // If the folder is empty, delete the folder
                            if (paths.count() == 0) {
                                Files.delete(yearFolderPath);
                            }
                        }
                    } else {
                        // Serialize the updated expenses list back to a string
                        String updatedContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(expenses);

                        // Write the updated content back to the file
                        Files.writeString(filePath, updatedContent);
                    }
                    return true;
                }
            } catch (IOException e) {
                throw new RuntimeException("Error processing file: " + filePath, e);
            }
        }

        // Return false if file doesn't exist or expense was not found
        return false;
    }

    public void add(ExpenseDto expenseDto) {
        save(expenseDto);
    }

    private void save(ExpenseDto expenseDto) throws RuntimeException {
        com.landlordpro.domain.Expense expense = expenseMapper.toEntity(expenseDto);
        try {
            expenseRepository.save(expense);
        } catch (DataIntegrityViolationException ex) {
            String errorMessage = "Constraint violation while saving expense=" + expenseDto.getId() + " User=" + expenseDto.getUserId();
            log.error(errorMessage, ex); // Assuming you have a logger in place
            throw new RuntimeException(errorMessage, ex);
        } catch (Exception ex) {
            String errorMessage = "Unexpected error while saving expense=" + expenseDto.getId() + " User=" + expenseDto.getUserId();
            log.error(errorMessage, ex); // Assuming you have a logger in place
            throw new RuntimeException(errorMessage, ex);
        }
    }
}
