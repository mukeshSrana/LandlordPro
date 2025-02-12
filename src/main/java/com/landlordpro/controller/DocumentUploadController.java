package com.landlordpro.controller;

import java.io.IOException;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/document")
@PreAuthorize("hasRole('ROLE_LANDLORD')")
public class DocumentUploadController {

    // This method handles the form submission for uploading files
    @PostMapping("/uploadTempReceipt")
    public String uploadTempReceipt(
        @RequestParam("receiptFile") MultipartFile receiptFile,
        @RequestParam("expenseId") UUID expenseId,
        HttpSession session,
        RedirectAttributes redirectAttributes) {

        try {
            // Save the uploaded file data to the session (temporary storage)
            byte[] fileData = receiptFile.getBytes();
            String fileName = receiptFile.getOriginalFilename();

            // Store the file data in the session under a key that includes the expenseId
            session.setAttribute("temporaryReceipt_" + expenseId, fileData);

            // Optionally, store the file name (if needed for later use)
            session.setAttribute("temporaryReceiptName_" + expenseId, fileName);

            redirectAttributes.addFlashAttribute("successMessage", "Receipt uploaded temporarily.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "File upload failed.");
            log.error("Error uploading file", e);
        }

        return "redirect:/expense/handle";
    }


    // This method displays the result after file upload
    @GetMapping("/uploadResult")
    public String uploadResult(Model model) {
        // This will be shown in the view (uploadResult.html)
        return "uploadResult";  // Return a Thymeleaf view
    }

    // This method serves the file upload form
    @GetMapping("/uploadForm")
    public String uploadForm() {
        // The form will be displayed (uploadForm.html)
        return "uploadForm";  // Return a Thymeleaf view
    }
}
