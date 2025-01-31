package com.landlordpro.service.document;


import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

@Service
public class TenantGdprGeneratorService {

    public byte[] generateGdprPdf(String tenantName, String landlordName, String landlordEmail, String landlordPhone) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph(" PRIVACY POLICY FOR TENANTS").simulateBold());
        document.add(new Paragraph("\n Attention " + tenantName).simulateBold());
        document.add(new Paragraph("\n1. About this policy"));
        document.add(new Paragraph("This privacy policy explains how your landlord, " + landlordName + ", processes your personal data in connection with the tenancy."));

        document.add(new Paragraph("\n2. Who is responsible for your personal data?"));
        document.add(new Paragraph("Your landlord, " + landlordName + ", is responsible for the data that is stored."));
        document.add(new Paragraph("LandlordPro is only a technical platform and processes data on behalf of the landlord."));

        document.add(new Paragraph("\n3. What personal data is stored?"));
        document.add(new Paragraph(" Name, address, phone number, and email\n" +
            " Information about the rental agreement\n" +
            " Expenses related to the tenancy\n" +
            " Documents such as the rental contract and receipts"));

        document.add(new Paragraph("\n7. Your rights"));
        document.add(new Paragraph("You have the right to request access, correction, and deletion of your data."));

        document.add(new Paragraph("\n Contact the landlord: " + landlordEmail));
        document.add(new Paragraph(" Phone: " + landlordPhone));


        document.close();
        return outputStream.toByteArray();
    }
}
