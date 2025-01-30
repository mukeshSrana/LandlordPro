package com.landlordpro.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.activation.DataSource;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final PdfGeneratorService pdfGeneratorService;

    public EmailService(JavaMailSender mailSender, PdfGeneratorService pdfGeneratorService) {
        this.mailSender = mailSender;
        this.pdfGeneratorService = pdfGeneratorService;
    }

    public byte[] generatePrivatePolicyPdf(String fromName, String fromEmail, String fromMobile ) {
        return pdfGeneratorService.generateGdprPdf(fromName, fromEmail, fromMobile);
    }

    public void sendPrivacyPolicyEmail(byte[] pdf, String toEmail, String toName, String fromName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("Personvernerklæring – Leieforhold hos " + fromName);
            helper.setText("Hei " + toName + ",\n\nHer er din personvernerklæring som PDF-vedlegg.");
            DataSource dataSource = new ByteArrayDataSource(pdf, "application/pdf");
            helper.addAttachment("Personvernerklæring.pdf", dataSource);

            mailSender.send(message);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error while sending private-policy(pdf) to " + toName + " from " + fromName);
        }
    }
}

