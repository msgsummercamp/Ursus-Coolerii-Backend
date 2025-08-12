package com.example.airassist.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PdfGenerator {

    private static final PDRectangle PAGE_SIZE = PDRectangle.A4;
    private static final float LINE_H = 14f;

    public static byte[] generateCasePdf(
            String contractId,
            String caseDate,
            String firstName,
            String lastName,
            String dateOfBirth,
            String phoneNumber,
            String address,
            String postalCode,
            String reservationNumber
    ) throws IOException {

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PAGE_SIZE);
            document.addPage(page);

            float width = PAGE_SIZE.getWidth();
            float height = PAGE_SIZE.getHeight();

            float marginL = mm(25);
            float marginR = mm(25);
            float marginT = mm(25);
            float marginB = mm(20);

            float xLeft = marginL;
            float xRight = width - marginR;
            float y = height - marginT;

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {

                y = drawCenteredText(cs, "AIRLINE COMPENSATION CASE REPORT", xLeft, xRight, y, PDType1Font.HELVETICA_BOLD, 16);
                y -= 8f;
                y = drawCenteredText(cs, "Case Documentation – Generated Automatically", xLeft, xRight, y, PDType1Font.HELVETICA_OBLIQUE, 10);
                y -= 20f;

                y = drawLabelValue(cs, "Case ID", nullSafe(contractId), xLeft, y, 100f);
                y = drawLabelValue(cs, "Case Date", nullSafe(caseDate), xLeft, y, 100f);
                y = drawLabelValue(cs, "Reservation Number", nullSafe(reservationNumber), xLeft, y, 150f);
                y -= 6f;

                y = sectionTitle(cs, "1. Passenger Information", xLeft, y);

                y = drawLabelValue(cs, "First Name", nullSafe(firstName), xLeft, y, 100f);
                y = drawLabelValue(cs, "Last Name", nullSafe(lastName), xLeft, y, 100f);
                y = drawLabelValue(cs, "Date of Birth", nullSafe(dateOfBirth), xLeft, y, 100f);
                y = drawLabelValue(cs, "Phone Number", nullSafe(phoneNumber), xLeft, y, 100f);

                drawText(cs, "Address:", xLeft, y, PDType1Font.HELVETICA_BOLD, 11);
                float addrX = xLeft + 100f;
                y = drawWrappedText(cs, nullSafe(address), addrX, y, xRight - addrX, PDType1Font.HELVETICA, 11, LINE_H) + 4f;

                y = drawLabelValue(cs, "Postal Code", nullSafe(postalCode), xLeft, y, 100f);
                y -= 6f;

                y = sectionTitle(cs, "2. Case Summary", xLeft, y);

                String summary =
                        "This document has been generated as part of the airline passenger rights process in connection with a reported " +
                                "flight disruption. The passenger has submitted a claim for compensation due to a cancellation, delay, or denied " +
                                "boarding, in line with applicable regulations, including EU Regulation (EC) No. 261/2004 where relevant. " +
                                "The personal details contained herein were provided by the passenger through the online claim form and are " +
                                "maintained securely within the case management system.";
                y = drawWrappedText(cs, summary, xLeft, y, xRight - xLeft, PDType1Font.HELVETICA, 11, LINE_H);

                y = sectionTitle(cs, "3. Notes", xLeft, y);

                y = bullet(cs, "Verify eligibility for compensation based on the flight details, disruption type, and the legal framework that applies.", xLeft, y, xRight);
                y = bullet(cs, "All subsequent correspondence should reference the Case ID stated above.", xLeft, y, xRight);

                float footerY = Math.max(y, marginB + 50f);
                drawText(cs, "Generated automatically by:", xLeft, footerY, PDType1Font.HELVETICA_BOLD, 10);
                drawText(cs, "Air-Assist Compensation Case Management System", xLeft + 160f, footerY, PDType1Font.HELVETICA, 10);

                footerY -= LINE_H;
                drawText(cs, "Date Generated:", xLeft, footerY, PDType1Font.HELVETICA_BOLD, 10);
                drawText(cs, nullSafe(caseDate), xLeft + 160f, footerY, PDType1Font.HELVETICA, 10);

                footerY -= (18);
                drawText(cs, "Signature:", xLeft, footerY, PDType1Font.HELVETICA_BOLD, 10);
                drawLine(cs, xLeft + 160f, footerY - 2f, xLeft + 380f, footerY - 2f);

                drawLine(cs, xLeft, marginB + 25f, xRight, marginB + 25f);
                rightAlignedText(cs, "Page 1 of 1", xRight, marginB + 10f, PDType1Font.HELVETICA, 9);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }


    private static float sectionTitle(PDPageContentStream cs, String text, float x, float y) throws IOException {
        y -= LINE_H;
        drawText(cs, text, x, y, PDType1Font.HELVETICA_BOLD, 12);
        return y - LINE_H;
    }

    private static float drawLabelValue(PDPageContentStream cs, String label, String value, float x, float y, float indent) throws IOException {
        drawText(cs, label + ":", x, y, PDType1Font.HELVETICA_BOLD, 11);
        drawText(cs, value, x + indent, y, PDType1Font.HELVETICA, 11);
        return y - LINE_H;
    }

    private static float bullet(PDPageContentStream cs, String text, float xLeft, float y, float xRight) throws IOException {
        y -= LINE_H;
        drawText(cs, "•", xLeft, y, PDType1Font.HELVETICA, 11);
        return drawWrappedText(cs, text, xLeft + 12f, y, (xRight - xLeft) - 12f, PDType1Font.HELVETICA, 11, LINE_H) + 4f;
    }

    private static void drawText(PDPageContentStream cs, String text, float x, float y,
                                 org.apache.pdfbox.pdmodel.font.PDFont font, int size) throws IOException {
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text != null ? text : "");
        cs.endText();
    }

    private static float drawWrappedText(PDPageContentStream cs, String text, float x, float y, float maxWidth,
                                         org.apache.pdfbox.pdmodel.font.PDFont font, int size, float leading) throws IOException {
        if (text == null) text = "";
        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        for (String w : words) {
            String candidate = (line + " " + w).trim();
            if (stringWidth(candidate, font, size) <= maxWidth) {
                line = new StringBuilder(candidate);
            } else {
                drawText(cs, line.toString(), x, y, font, size);
                y -= leading;
                line = new StringBuilder(w);
            }
        }
        if (line.length() > 0) {
            drawText(cs, line.toString(), x, y, font, size);
            y -= leading;
        }
        return y;
    }

    private static void drawLine(PDPageContentStream cs, float x1, float y1, float x2, float y2) throws IOException {
        cs.moveTo(x1, y1);
        cs.lineTo(x2, y2);
        cs.stroke();
    }

    private static void rightAlignedText(PDPageContentStream cs, String text, float xRight, float y,
                                         org.apache.pdfbox.pdmodel.font.PDFont font, int size) throws IOException {
        float w = stringWidth(text, font, size);
        drawText(cs, text, xRight - w, y, font, size);
    }

    private static float stringWidth(String s, org.apache.pdfbox.pdmodel.font.PDFont font, int size) throws IOException {
        if (s == null) return 0f;
        return font.getStringWidth(s) / 1000f * size;
    }

    private static float mm(float mm) {
        return mm * 72f / 25.4f;
    }

    private static String nullSafe(String v) {
        return (v == null || v.isBlank()) ? "-" : v;
    }

    private static float drawCenteredText(PDPageContentStream cs, String text,
                                          float xLeft, float xRight, float y,
                                          org.apache.pdfbox.pdmodel.font.PDFont font,
                                          int size) throws IOException {
        float textWidth = stringWidth(text, font, size);
        float startX = xLeft + ((xRight - xLeft) - textWidth) / 2;
        drawText(cs, text, startX, y, font, size);
        return y - (size + (LINE_H - size));
    }

}
