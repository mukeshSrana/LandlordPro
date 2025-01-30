package com.landlordpro.service;


import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

@Service
public class PdfGeneratorService {

    public byte[] generateGdprPdf(String landlordName, String landlordEmail, String landlordPhone) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("📜 PERSONVERNERKLÆRING FOR LEIETAKERE").simulateBold());
        document.add(new Paragraph("\n1. Om denne erklæringen"));
        document.add(new Paragraph("Denne personvernerklæringen forklarer hvordan din utleier, " + landlordName + ", behandler dine personopplysninger i forbindelse med leieforholdet."));

        document.add(new Paragraph("\n2. Hvem er ansvarlig for dine personopplysninger?"));
        document.add(new Paragraph("Din utleier, " + landlordName + ", er ansvarlig for opplysningene som lagres."));
        document.add(new Paragraph("LandlordPro er kun en teknisk plattform og behandler data på vegne av utleieren."));

        document.add(new Paragraph("\n3. Hvilke personopplysninger lagres?"));
        document.add(new Paragraph("✅ Navn, adresse, telefonnummer og e-post\n" +
            "✅ Opplysninger om leieavtalen\n" +
            "✅ Utgifter knyttet til leieforholdet\n" +
            "✅ Dokumenter som leiekontrakt og kvitteringer"));

        document.add(new Paragraph("\n7. Dine rettigheter"));
        document.add(new Paragraph("Du har rett til å be om innsyn, korrigering og sletting av dine data."));

        document.add(new Paragraph("\n📧 Kontakt utleier: " + landlordEmail));
        document.add(new Paragraph("📞 Telefon: " + landlordPhone));

        document.close();
        return outputStream.toByteArray();
    }
}
