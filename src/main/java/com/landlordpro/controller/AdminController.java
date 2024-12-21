package com.landlordpro.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.landlordpro.service.ContactService;
import com.landlordpro.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final ContactService contactService;

    public AdminController(UserService userService, ContactService contactService) {
        this.userService = userService;
        this.contactService = contactService;
    }

//    @GetMapping("/editUser/{id}")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public String editUser(@PathVariable Long id, Model model) {
//        User user = userService.getUserById(id); // Fetch the user by ID
//        model.addAttribute("user", user); // Add the user to the model for editing
//        return "edit-user"; // Return the view name for editing user
//    }


    // Restricting access at the controller level
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users")
    public String getAllUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("page", "userAdmin");
        return "userAdmin";
    }

    // Restricting access at the controller level
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/contacts")
    public String getAllMessages(Model model) {
        model.addAttribute("contacts", contactService.getAllContacts());
        model.addAttribute("page", "contactAdmin");
        return "contactAdmin";
    }
}
