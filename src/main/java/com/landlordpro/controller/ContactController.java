package com.landlordpro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.landlordpro.dto.ContactDto;
import com.landlordpro.service.ContactService;

import jakarta.validation.Valid;

@Controller
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/contact")
    public String showContactForm() {
        return "contact";
    }

    @PostMapping("/contact")
    public String submitContact(@Valid @ModelAttribute ContactDto contactDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            // Return to the form with error messages
            model.addAttribute("contact", contactDto);
            return "contact";
        }

        contactService.saveContact(contactDto);

        // Pass a success message to the next view
        model.addAttribute("successMessage", "Thank you for contacting us, " + contactDto.getName() + "! We will get back to you soon.");

        // Redirect or load a success page
        return "contact"; // Renders contactDto.html with success message
    }
}

