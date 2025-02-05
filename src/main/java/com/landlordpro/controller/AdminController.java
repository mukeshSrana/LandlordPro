package com.landlordpro.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.landlordpro.dto.ContactDto;
import com.landlordpro.dto.UserDto;
import com.landlordpro.dto.enums.UserRole;
import com.landlordpro.service.ContactService;
import com.landlordpro.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final UserService userService;
    private final ContactService contactService;

    public AdminController(UserService userService, ContactService contactService) {
        this.userService = userService;
        this.contactService = contactService;
    }

    @PostMapping("/update/user")
    public String updateUser(@ModelAttribute UserDto userDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean updated = userService.updateUser(userDto);
            if (updated) {
                response.put("success", true);
                response.put("userUpdated", updated);
            } else {
                response.put("success", false);
                response.put("message", "Update failed: user not found or could not be updated.");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        return  "redirect:/admin/users";
    }

    @PostMapping("/update/contact")
    public String updateContact(@ModelAttribute ContactDto contactDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean updated = contactService.updateContact(contactDto);
            if (updated) {
                response.put("success", true);
                response.put("contactUpdated", updated);
            } else {
                response.put("success", false);
                response.put("message", "Update failed: contact not found or could not be updated.");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            log.error(e.getMessage(), e);
        }
        return  "redirect:/admin/contacts";
    }

    @GetMapping("/users")
    public String getAllUsers(Model model) {
        model.addAttribute("allRoles", UserRole.values());
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("page", "userAdmin");
        return "userAdmin";
    }

    @GetMapping("/contacts")
    public String getAllMessages(Model model) {
        model.addAttribute("contacts", contactService.getAllContacts());
        model.addAttribute("page", "contactAdmin");
        return "contactAdmin";
    }
}
