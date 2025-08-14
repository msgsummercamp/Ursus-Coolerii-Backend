package com.example.airassist.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PdfGenerator {

    private static final PDRectangle PAGE_SIZE = PDRectangle.A4;
    private static final float LINE_H = 16f;

    public static byte[] generateCasePdf(
            String contractId,
            String caseDate,
            String firstName,
            String lastName,
            String reservationNumber
    ) throws IOException {

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PAGE_SIZE);
            document.addPage(page);

            PDFont fontRegular = loadUnicodeFont(document);
            PDFont fontBold    = PDType1Font.HELVETICA_BOLD;
            PDFont fontItalic  = PDType1Font.HELVETICA_OBLIQUE;

            float width  = PAGE_SIZE.getWidth();
            float height = PAGE_SIZE.getHeight();

            float marginL = mm(25);
            float marginR = mm(25);
            float marginT = mm(20);
            float marginB = mm(20);

            float xLeft  = marginL;
            float xRight = width - marginR;
            float y = height - marginT;

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {

                y = drawCenteredText(cs, "TRANSFER FORM (AGREEMENT) / FORMULAR DE CESIUNE (ACORD)",
                        xLeft, xRight, y, fontBold, 14);
                y -= 8f;

                y = drawTextLine(cs,
                        "No. " + nullSafe(contractId) + "/" + nullSafe(caseDate),
                        xLeft, y, fontRegular, 12);

                y -= 14f;

                drawLine(cs, xLeft, y, xRight, y);
                y -= 18f;

                y = drawValueWithHint(cs,
                        (nullSafe(firstName) + " " + nullSafe(lastName)).trim(),
                        "First and Last name of the passenger (the “Client”) / Prenume și nume pasager („Client”)",
                        xLeft, y, xRight - xLeft, fontRegular);

                y -= 10f;

                y = drawValueWithHint(cs,
                        nullSafe(reservationNumber),
                        "Booking reference / Cod de rezervare",
                        xLeft, y, xRight - xLeft, fontRegular);

                y -= 6f;

                y -= 10f;

                String en1 = "The client hereby agrees to transmit all rights and obligations relating to the flights under the reservation identified above as defined in Regulation (EC) no. 261/2004 and the Montreal Convention of 1999, consisting of requesting and receiving the compensation provided for in the present Regulation to Passanger Services s.r.l. (“Air-Assist”).";
                y = drawWrappedText(cs, en1, xLeft, y, xRight - xLeft, fontRegular, 11, LINE_H);

                String en2 = "The client undertakes not to take any act or action to prevent Air-Assist from exercising its right from the date of signature of the present agreement, not to surrender the rights and obligations set out above to other natural or legal persons, not to approach, either directly or indirectly, the airline in relation to taking advantage of the right to compensation, and not to accept any contact or direct payment from the airline.";
                y = drawWrappedText(cs, en2, xLeft, y, xRight - xLeft, fontRegular, 11, LINE_H);

                String en3 = "The client declares that he/she is the sole holder of the rights and obligations submitted and has not transferred such rights and obligations to other natural or legal persons, so far there has been no cause for extinction of the right to compensation, and that there did not exist and do not exist any proceedings before the courts in relation to the rights and obligations transmitted.";
                y = drawWrappedText(cs, en3, xLeft, y, xRight - xLeft, fontRegular, 11, LINE_H);

                String en4 = "The client acknowledged that, based on this agreement, Air-Assist has the exclusive right:";
                y = drawWrappedText(cs, en4, xLeft, y, xRight - xLeft, fontRegular, 11, LINE_H);

                y = bullet(cs, "To make any necessary action in front of any third party for the purpose of exercising the right to compensation", xLeft, y, xRight, fontRegular);
                y = bullet(cs, "To obtain any kind of necessary information, as well as to initiate requests for information on any civil, administrative, etc. procedures, and to initiate any judicial or administrative actions before the bodies responsible for the enforcement of the legislation on passenger rights", xLeft, y, xRight, fontRegular);
                y = bullet(cs, "To initiate, direct and undertake any type of negotiation, judicial or extrajudicial procedures for the receipt of compensation", xLeft, y, xRight, fontRegular);
                y = bullet(cs, "To request from the airline not to process the client’s personal data, but solely for the purpose of verifying the claim for compensation", xLeft, y, xRight, fontRegular);
                y = bullet(cs, "To receive payments in connection with the claim for compensation", xLeft, y, xRight, fontRegular);

                String en5 = "The client confirms by signing and by the present act that he/she has read and accepted the Air-Assist General Terms as published on their Air-Assist website.";
                y = drawWrappedText(cs, en5, xLeft, y, xRight - xLeft, fontRegular, 11, LINE_H);

                y -= 4f;

                String ro1 = "Prin prezentul acord clientul este de acord să transmită toate drepturile și obligațiile aferente zborurilor cu nr. de rezervare identificat mai sus,conferite de Regulamentul (CE) nr. 261/2004 și Convenția de la Montreal din 1999, constând în solicitarea și încasarea compensației prevăzute de acest Regulament, către Societatea Passanger Services s.r.l („Air-Assist”).";
                y = drawWrappedText(cs, ro1, xLeft, y, xRight - xLeft, fontRegular, 11, LINE_H);

                String ro2 = "Clientul se obligă ca, de la data semnării prezentului, să nu intreprindă niciun act sau fapt  prin care  să o  împiedice Air-Assist în realizarea dreptului său, să nu mai  transmită drepturile și obligațiile prevăzute mai sus altorpersoane fizice sau juridice, să nu facă niciun demers direct sau indirect la compania aeriană  în vederea valorificării dreptului la compensație și să nu accepte niciun contact sau plată directă de la compania aeriană.";
                y = drawWrappedText(cs, ro2, xLeft, y, xRight - xLeft, fontRegular, 11, LINE_H);

                String ro3 = "Clientul declară că este unicul titular al drepturilor și obligațiilor transmise  și că nu a mai transmis  aceaste drepturi și obligații  altor persoane fizice sau juridice, că, până în present, nu a intervenit nicio cauză de stingere a dreptului la compensație  și că nu a existat și nu există niciun proces pe rolul instanțelor judecătorești în legătură cu drepturile și obligațiile transmise.";
                y = drawWrappedText(cs, ro3, xLeft, y, xRight - xLeft, fontRegular, 11, LINE_H);

                String ro4 = "Clientul a luat la cunoștință că pe baza prezentului acord Air-Assist  are drept exclusiv :";
                y = drawWrappedText(cs, ro4, xLeft, y, xRight - xLeft, fontRegular, 11, LINE_H);

                y = bullet(cs, "De a face orice demers necesar  în fața oricărei terțe persoane în vederea valorificării dreptului la compensație", xLeft, y, xRight, fontRegular);
                y = bullet(cs, "De a obține orice tip de informații necesare, precum și de a iniția solicitări de informare cu privire la orice procedeu civil, administrativ etc. și de a iniția orice demers judiciar sau administrativ în fața organelor responsabile de aplicare a legislației referitoare la  dreptul pasagerilor", xLeft, y, xRight, fontRegular);
                y = bullet(cs, "De a iniția, dirija și întreprinde orice tip de negociere, procedee judiciare sau extrajudiciare pentru încasarea compensației", xLeft, y, xRight, fontRegular);
                y = bullet(cs, "De a solicita companiei aeriene  să nu proceseze datele personale ale clientului decât exclusiv în scopul verificării cererii de compensație", xLeft, y, xRight, fontRegular);
                y = bullet(cs, "De a încasa plăți în legătură cu cererea de compensație.", xLeft, y, xRight, fontRegular);

                String ro5 = "Clientul   confirmă prin semnare și  prin actul de față că a citit si  a acceptat Condițiile generale   ale  Air-Assist, așa cum au fost  publicate pe site-ul Air-Assist – air-assist.eu.";
                y = drawWrappedText(cs, ro5, xLeft, y, xRight - xLeft, fontRegular, 11, LINE_H);

                y -= 18f;

                String contact = "For any questions, please write on the following e-mail address / Pentru orice întrebări, scrieți-ne pe adresa: help@air-assist.eu";
                y = drawWrappedText(cs, contact, xLeft, y, xRight - xLeft, fontItalic, 10, LINE_H - 2);

                String corpAdr = "Corporate address / Sediu social: Societatea Passanger Services S.R.L., str. Suceava nr. 8, Cluj-Napoca, 400219, jud. Cluj, România";
                y = drawWrappedText(cs, corpAdr, xLeft, y, xRight - xLeft, fontItalic, 10, LINE_H - 2);

                float sigBlockTop = Math.max(y - 14f, marginB + 70f);

                drawText(cs, "Client signature / Semnătură Client", xLeft, sigBlockTop, fontRegular, 11);
                drawText(cs, "Date / Data", xLeft + 320f, sigBlockTop, fontRegular, 11);

                float sigLineY = sigBlockTop - 8f;
                drawLine(cs, xLeft, sigLineY, xLeft + 280f, sigLineY);
                drawLine(cs, xLeft + 320f, sigLineY, xLeft + 480f, sigLineY);

                drawText(cs, (nullSafe(firstName) + " " + nullSafe(lastName)).trim(), xLeft, sigLineY - 12f, fontItalic, 9);
                drawText(cs, nullSafe(caseDate), xLeft + 320f, sigLineY - 12f, fontItalic, 9);

                drawLine(cs, xLeft, marginB + 28f, xRight, marginB + 28f);
                rightAlignedText(cs, "Page 1 of 1", xRight, marginB + 10f, PDType1Font.HELVETICA, 9);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }


    private static PDFont loadUnicodeFont(PDDocument document) throws IOException {
        try (InputStream is = PdfGenerator.class.getResourceAsStream("/fonts/DejaVuSans.ttf")) {
            if (is != null) {
                return PDType0Font.load(document, is, true);
            }
        }
        return PDType1Font.HELVETICA;
    }

    private static float drawTextLine(PDPageContentStream cs, String text, float x, float y,
                                      PDFont font, int size) throws IOException {
        drawText(cs, text, x, y, font, size);
        return y - LINE_H;
    }

    private static float drawValueWithHint(PDPageContentStream cs, String value, String hint,
                                           float x, float y, float maxWidth, PDFont font) throws IOException {
        y = drawWrappedText(cs, value, x, y, maxWidth, font, 12, LINE_H);
        return drawWrappedText(cs, hint, x, y, maxWidth, font, 10, LINE_H - 2);
    }

    private static float bullet(PDPageContentStream cs, String text, float xLeft, float y, float xRight,
                                PDFont font) throws IOException {
        y -= LINE_H;
        drawText(cs, "•", xLeft, y, font, 11);
        return drawWrappedText(cs, text, xLeft + 12f, y, (xRight - xLeft) - 12f, font, 11, LINE_H) + 4f;
    }

    private static void drawText(PDPageContentStream cs, String text, float x, float y,
                                 PDFont font, int size) throws IOException {
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text != null ? text : "");
        cs.endText();
    }

    private static float drawWrappedText(PDPageContentStream cs, String text, float x, float y, float maxWidth,
                                         PDFont font, int size, float leading) throws IOException {
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
                                         PDFont font, int size) throws IOException {
        float w = stringWidth(text, font, size);
        drawText(cs, text, xRight - w, y, font, size);
    }

    private static float stringWidth(String s, PDFont font, int size) throws IOException {
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
                                          PDFont font, int size) throws IOException {
        float textWidth = stringWidth(text, font, size);
        float startX = xLeft + ((xRight - xLeft) - textWidth) / 2;
        drawText(cs, text, startX, y, font, size);
        return y - (size + (LINE_H - size));
    }
}
