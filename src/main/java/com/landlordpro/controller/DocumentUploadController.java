package com.landlordpro.controller;

import java.io.IOException;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/uploadTempReceipt")
@PreAuthorize("hasRole('ROLE_LANDLORD')")
public class DocumentUploadController {

    // This method handles the form submission for uploading files
    @PostMapping
    public String uploadTempReceipt(
        @RequestParam("receiptFile") MultipartFile receiptFile,
        @RequestParam("expenseId") UUID expenseId,
        @RequestParam("apartmentId") UUID apartmentId,
        @RequestParam("year") Integer year,
        HttpSession session,
        RedirectAttributes redirectAttributes) {

        try {
            // Save the uploaded file data to the session (temporary storage)
            byte[] fileData = receiptFile.getBytes();
            // Store the file data in the session under a key that includes the expenseId
            session.setAttribute("temporaryReceipt_" + expenseId, fileData);

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            log.error(e.getMessage(), e);
        }

        redirectAttributes.addFlashAttribute("page", "handleExpense");
        return "redirect:/expense/handle?year=" + year + "&apartmentId=" + apartmentId;
    }
}
