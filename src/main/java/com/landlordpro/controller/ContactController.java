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

    @PostMapping("/contact")
    public String submitContact(@RequestParam String name, @RequestParam String email, @RequestParam String message, Model model) {
        // Log or save the data
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Message: " + message);

        // Pass a success message to the next view
        model.addAttribute("successMessage", "Thank you for contacting us! We will get back to you soon.");

        // Redirect or load a success page
        return "contact"; // Renders thank-you.html
    }
}

