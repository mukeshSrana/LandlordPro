package com.landlordpro.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.landlordpro.model.Expense;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    private final Path basePath = Paths.get("./data/expenses");
    private final ObjectMapper objectMapper;

    public ExpenseService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void saveExpense(Expense expense) throws IOException {
        Files.createDirectories(basePath);
        String fileName = UUID.randomUUID() + ".json";
        Path filePath = basePath.resolve(fileName);
        Files.write(filePath, Collections.singleton(objectMapper.writeValueAsString(expense)));
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
