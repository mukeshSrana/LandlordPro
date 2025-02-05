package com.landlordpro.controller;

import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.landlordpro.dto.ContactDto;
import com.landlordpro.service.ContactService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/contact")
    public String showContactForm(Model model) {
        Random random = new Random();
        int randomNumber = random.nextInt(1000000);
        String reference = "REF-" + String.format("%06d", randomNumber);
        ContactDto contactDto = new ContactDto();
        contactDto.setReference(reference);
        model.addAttribute("contact", contactDto);
        return "contact";
    }

    @PostMapping("/contact")
    public String submitContact(@Valid @ModelAttribute("contact") ContactDto contactDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            // Return to the form with error messages
            model.addAttribute("contact", contactDto);
            return "contact";
        }

        try {
            contactService.saveContact(contactDto);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            log.error(e.getMessage(), e.getCause());
        }

        // Pass a success message to the next view
        model.addAttribute("successMessage", "Thank you for contacting us, " + contactDto.getName() + "! We will get back to you soon.");

        // Redirect or load a success page
        return "redirect:/contact";
    }
}

