package com.landlordpro.controller;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.landlordpro.config.AppConfig;
import com.landlordpro.model.Apartment;
import com.landlordpro.service.ApartmentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ApartmentController {
    private final AppConfig appConfig;
    private final ApartmentService apartmentService;

    public ApartmentController(AppConfig appConfig,  ApartmentService apartmentService) {
        this.appConfig = appConfig;
        this.apartmentService = apartmentService;
    }

    @PostMapping("/saveApartment")
    public String saveExpense(@ModelAttribute Apartment apartment, Model model) {
        try {
            if (apartmentService.isExists(apartment)) {
                model.addAttribute("errorMessage", "Apartment with this name already exists.");
                return "registerApartment"; // Return to the form with error message
            }
            apartmentService.save(apartment);
            model.addAttribute("successMessage", "Apartment created successfully!");
        } catch (FileAlreadyExistsException e) {
            model.addAttribute("errorMessage", "Error: Apartment with this name already exists.");
            log.error("Apartment creation failed: " + e.getMessage());  // Log specific error message
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Error creating apartment: " + e.getMessage());
            log.error("IOException while saving apartment: " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Unexpected error occurred: " + e.getMessage());
            log.error("Unexpected error: ", e);
        }
        return "registerApartment";
    }

    @GetMapping("/registerApartment")
    public String registerApartment(Model model) {
        model.addAttribute("page", "registerApartment");
        return "registerApartment";
    }

    @GetMapping("/handleApartment")
    public String handleApartment(Model model) {
        model.addAttribute("apartments", apartmentService.apartments());
        model.addAttribute("page", "handleApartment");
        return "handleApartment";
    }
}

