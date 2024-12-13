package com.landlordpro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ContactController {

    @GetMapping("/contact")
    public String showContactForm() {
        return "contact";
    }

    @PostMapping("/submit-contact")
    public String submitContactForm(@RequestParam String name,
        @RequestParam String email,
        @RequestParam String message,
        Model model) {
        // Handle form submission (e.g., send email or save to database)
        model.addAttribute("successMessage", "Thank you, " + name + "! Your message has been received.");
        return "contact";
    }
}

