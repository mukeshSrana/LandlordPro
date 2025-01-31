package com.landlordpro.service;

import java.io.IOException;
import java.util.Base64;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import jakarta.activation.DataSource;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService {
    private final String SENDGRID_API_KEY = "SG.2KYRy1cRSV6223xKz3vKDQ.4JF4xKlitdZgHD2nazlU4eZgYjM1GmWyJ0Y46GNj0ak1";

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
            Email from = new Email("mukesh.s.rana@hotmail.com", fromName);
            Email to = new Email(toEmail, toName);
            String subject = "Personvernerklæring – Leieforhold hos " + fromName;
            Content content = new Content("text/plain", "Hei " + toName + ",\n\nHer er din personvernerklæring som PDF-vedlegg.");
            Mail mail = new Mail(from, subject, to, content);

            Attachments attachment = new Attachments();
            attachment.setContent(Base64.getEncoder().encodeToString(pdf));
            attachment.setType("application/pdf");
            attachment.setFilename("Personvernerklæring.pdf");
            attachment.setDisposition("attachment");
            mail.addAttachments(attachment);

            SendGrid sg = new SendGrid(SENDGRID_API_KEY);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            if (response.getStatusCode() != 200) {
                throw new RuntimeException("Failed to send email, status code: " + response.getStatusCode());
            }

        } catch (IOException ex) {
            throw new RuntimeException("Error while sending privacy-policy email to " + toName, ex);
        }
    }

    public void sendPrivacyPolicyEmail1(byte[] pdf, String toEmail, String toName, String fromName) {
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

