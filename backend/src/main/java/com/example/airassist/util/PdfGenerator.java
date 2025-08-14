package com.example.airassist.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PdfGenerator {

    private static final PDRectangle PAGE_SIZE = PDRectangle.A4;
    private static final float LINE_H = 14f;


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

            float width = PAGE_SIZE.getWidth();
            float height = PAGE_SIZE.getHeight();

            float marginL = mm(20);
            float marginR = mm(20);
            float marginT = mm(22);
            float marginB = mm(20);

            float xLeft  = marginL;
            float xRight = width - marginR;
            float contentW = xRight - xLeft;

            float sigLineY = marginB + 120f;
            float contactY1 = marginB + 60f;
            float contactY2 = contactY1 - 16f;

            float y = height - marginT;

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {


                float titleY = y - 8f;
                y = drawCenteredText(cs,
                        "TRANSFER FORM (AGREEMENT) / FORMULAR DE CESIUNE (ACORD)",
                        xLeft, xRight, titleY, PDType1Font.HELVETICA_BOLD, 13);
                y -= 2f;

                String noLine = "No. " + nullSafe(contractId) + "/" + nullSafe(caseDate);
                y = drawCenteredText(cs, noLine, xLeft, xRight, y, PDType1Font.HELVETICA_BOLD, 11);
                y -= 14f;

                float gap = mm(30);
                float colW = (contentW - gap) / 2f;
                float nameX = xLeft;
                float refX  = xLeft + colW + gap;

                String fullName = (nullSafe(firstName) + " " + nullSafe(lastName)).trim();
                drawText(cs, fullName, nameX, y, PDType1Font.HELVETICA, 12);
                drawText(cs, nullSafe(reservationNumber), refX, y, PDType1Font.HELVETICA, 12);
                y -= 10f;

                float underlineY = y;
                drawLine(cs, nameX, underlineY, nameX + colW, underlineY);
                drawLine(cs, refX,  underlineY, refX  + colW, underlineY);
                y -= (LINE_H - 4f);

                drawText(cs, "First and Last name of the passenger /", nameX, y, PDType1Font.HELVETICA_BOLD, 10);
                drawText(cs, "Prenume si nume pasager", nameX, y - 12f, PDType1Font.HELVETICA, 10);

                drawText(cs, "Reservation number /", refX, y, PDType1Font.HELVETICA_BOLD, 10);
                drawText(cs, "Cod de rezervare",    refX, y - 12f, PDType1Font.HELVETICA, 10);

                y -= (12f + 10f);

                drawLine(cs, xLeft, y, xRight, y);
                y -= 12f;


                float gutter2   = mm(8);
                float contentW2 = xRight - xLeft;
                float colW2     = (contentW2 - gutter2) / 2f;
                float colLX2    = xLeft;
                float colRX2    = xLeft + colW2 + gutter2;

                float colStartY2 = y;

                float bottomLimit2 = sigLineY + 42f;

                final int FS = 10;
                final float LEADING = FS + 2f;

                class _ColWrap {
                    float para(String text, float x, float y0, float maxW) throws IOException {
                        if (text == null || text.isEmpty()) return y0;
                        float yLoc = y0;
                        String[] words = text.split("\\s+");
                        StringBuilder line = new StringBuilder();
                        for (String w : words) {
                            String cand = (line + " " + w).trim();
                            if (stringWidth(cand, PDType1Font.HELVETICA, FS) <= maxW) {
                                line = new StringBuilder(cand);
                            } else {
                                if (yLoc <= bottomLimit2) return bottomLimit2;
                                drawText(cs, line.toString(), x, yLoc, PDType1Font.HELVETICA, FS);
                                yLoc -= LEADING;
                                if (yLoc <= bottomLimit2) return bottomLimit2;
                                line = new StringBuilder(w);
                            }
                        }
                        if (line.length() > 0 && yLoc > bottomLimit2) {
                            drawText(cs, line.toString(), x, yLoc, PDType1Font.HELVETICA, FS);
                            yLoc -= LEADING;
                        }
                        return Math.max(yLoc, bottomLimit2);
                    }

                    float bullet(String text, float x, float y0, float maxW) throws IOException {
                        float yLoc = y0 - LEADING;
                        if (yLoc <= bottomLimit2) return bottomLimit2;
                        drawText(cs, "-", x, yLoc, PDType1Font.HELVETICA, FS);
                        return para(text, x + 12f, yLoc, maxW - 12f) + 2f;
                    }
                }
                _ColWrap cw = new _ColWrap();

                float yL2 = colStartY2;
                yL2 = cw.para(
                        "The client hereby agrees to transmit all rights and obligations relating to the flights under the reservation identified above as defined in Regulation (EC) no. 261/2004 and the Montreal Convention of 1999, consisting of requesting and receiving the compensation provided for in the present Regulation to Passanger Services S.R.L. (\"Air-Assist\").",
                        colLX2, yL2, colW2);
                yL2 = cw.para(
                        "The client undertakes not to take any act or action to prevent Air-Assist from exercising its right from the date of signature of the present agreement, not to surrender the rights and obligations set out above to other natural or legal persons, not to approach, either directly or indirectly, the airline in relation to taking advantage of the right to compensation, and not to accept any contact or direct payment from the airline.",
                        colLX2, yL2, colW2);
                yL2 = cw.para(
                        "The client declares that he/she is the sole holder of the rights and obligations submitted and has not transferred such rights and obligations to other natural or legal persons, so far there has been no cause for extinction of the right to compensation, and that there did not exist and do not exist any proceedings before the courts in relation to the rights and obligations transmitted.",
                        colLX2, yL2, colW2);
                yL2 = cw.para(
                        "The client acknowledged that, based on this agreement, Air-Assist has the exclusive right:",
                        colLX2, yL2, colW2);

                yL2 = cw.bullet("To make any necessary action in front of any third party for the purpose of exercising the right to compensation",
                        colLX2, yL2, colW2);
                yL2 = cw.bullet("To obtain any kind of necessary information, as well as to initiate requests for information on any civil, administrative, etc. procedures, and to initiate any judicial or administrative actions before the bodies responsible for the enforcement of the legislation on passenger rights",
                        colLX2, yL2, colW2);
                yL2 = cw.bullet("To initiate, direct and undertake any type of negotiation, judicial or extrajudicial procedures for the receipt of compensation",
                        colLX2, yL2, colW2);
                yL2 = cw.bullet("To request from the airline not to process the client's personal data, but solely for the purpose of verifying the claim for compensation",
                        colLX2, yL2, colW2);
                yL2 = cw.bullet("To receive payments in connection with the claim for compensation",
                        colLX2, yL2, colW2);

                yL2 = cw.para(
                        "The client confirms by signing and by the present act that he/she has read and accepted the Air-Assist General Terms as published on their Air-Assist website.",
                        colLX2, yL2, colW2);

                float yR2 = colStartY2;
                yR2 = cw.para(
                        "Prin prezentul acord clientul este de acord sa transmita toate drepturile si obligatiile aferente zborurilor cu nr. de rezervare identificat mai sus, conferite de Regulamentul (CE) nr. 261/2004 si Conventia de la Montreal din 1999, constand in solicitarea si incasarea compensatiei prevazute de acest Regulament, catre Societatea Passanger Services S.R.L. (\"Air-Assist\").",
                        colRX2, yR2, colW2);
                yR2 = cw.para(
                        "Clientul se obliga ca, de la data semnarii prezentului, sa nu intreprinda niciun act sau fapt prin care sa o impiedice Air-Assist in realizarea dreptului sau, sa nu mai transmita drepturile si obligatiile prevazute mai sus altor persoane fizice sau juridice, sa nu faca niciun demers direct sau indirect la compania aeriana in vederea valorificarii dreptului la compensatie si sa nu accepte niciun contact sau plata directa de la compania aeriana.",
                        colRX2, yR2, colW2);
                yR2 = cw.para(
                        "Clientul declara ca este unicul titular al drepturilor si obligatiilor transmise si ca nu a mai transmis aceste drepturi si obligatii altor persoane fizice sau juridice, ca, pana in prezent, nu a intervenit nicio cauza de stingere a dreptului la compensatie si ca nu a existat si nu exista niciun proces pe rolul instantelor judecatoresti in legatura cu drepturile si obligatiile transmise.",
                        colRX2, yR2, colW2);
                yR2 = cw.para(
                        "Clientul a luat la cunostinta ca pe baza prezentului acord Air-Assist are drept exclusiv:",
                        colRX2, yR2, colW2);

                yR2 = cw.bullet("De a face orice demers necesar in fata oricarei terte persoane in vederea valorificarii dreptului la compensatie",
                        colRX2, yR2, colW2);
                yR2 = cw.bullet("De a obtine orice tip de informatii necesare, precum si de a initia solicitari de informare cu privire la orice procedeu civil, administrativ etc. si de a initia orice demers judiciar sau administrativ in fata organelor responsabile de aplicare a legislatiei referitoare la dreptul pasagerilor",
                        colRX2, yR2, colW2);
                yR2 = cw.bullet("De a initia, dirija si intreprinde orice tip de negociere, procedee judiciare sau extrajudiciare pentru incasarea compensatiei",
                        colRX2, yR2, colW2);
                yR2 = cw.bullet("De a solicita companiei aeriene sa nu proceseze datele personale ale clientului decat exclusiv in scopul verificarii cererii de compensatie",
                        colRX2, yR2, colW2);
                yR2 = cw.bullet("De a incasa plati in legatura cu cererea de compensatie.",
                        colRX2, yR2, colW2);

                yR2 = cw.para(
                        "Clientul confirma prin semnare si prin actul de fata ca a citit si a acceptat Conditiile generale ale Air-Assist, asa cum au fost publicate pe site-ul Air-Assist - air-assist.eu.",
                        colRX2, yR2, colW2);

                y = Math.min(yL2, yR2) - mm(4);


                float centerX   = xLeft + (xRight - xLeft) / 2f;
                float linesGap  = mm(22);
                float lineWidth = Math.min(220f, (xRight - xLeft - linesGap) / 2f);

                float leftLineX2  = centerX - (linesGap / 2f);
                float leftLineX1  = leftLineX2 - lineWidth;
                float rightLineX1 = centerX + (linesGap / 2f);
                float rightLineX2 = rightLineX1 + lineWidth;

                drawLine(cs, leftLineX1,  sigLineY, leftLineX2,  sigLineY);
                drawLine(cs, rightLineX1, sigLineY, rightLineX2, sigLineY);

                float labelY = sigLineY - 16f;
                drawCenteredBetween(cs, "Client signature / Semnatura Client",
                        leftLineX1, leftLineX2, labelY, PDType1Font.HELVETICA_BOLD, 10);
                drawCenteredBetween(cs, "Date / Data",
                        rightLineX1, rightLineX2, labelY, PDType1Font.HELVETICA_BOLD, 10);

                float maxTextWidth = xRight - xLeft;
                float cx = xLeft;
                float yPtr = contactY1;

                String prefix = "For any questions, please write on the following e-mail address / Pentru orice intrebari, scrieti-ne pe adresa: ";
                {
                    String[] words = prefix.split("\\s+");
                    StringBuilder line = new StringBuilder();
                    for (String w : words) {
                        String cand = (line + " " + w).trim();
                        if (stringWidth(cand, PDType1Font.HELVETICA_BOLD, 10) <= maxTextWidth) {
                            line = new StringBuilder(cand);
                        } else {
                            drawText(cs, line.toString(), cx, yPtr, PDType1Font.HELVETICA_BOLD, 10);
                            yPtr -= (LINE_H - 2);
                            line = new StringBuilder(w);
                        }
                    }
                    if (line.length() > 0) {
                        drawText(cs, line.toString(), cx, yPtr, PDType1Font.HELVETICA_BOLD, 10);
                        yPtr -= (LINE_H - 2);
                    }
                    drawText(cs, "help@air-assist.eu", cx, yPtr, PDType1Font.HELVETICA_BOLD, 10);
                    yPtr -= (LINE_H - 2);
                }

                String corpLabel = "Corporate address / Sediu social: ";
                String corpBody  = "Societatea Passanger Services S.R.L., str. Suceava nr. 8, Cluj-Napoca, 400219, jud. Cluj, Romania";

                float labelW = stringWidth(corpLabel, PDType1Font.HELVETICA_BOLD, 10);
                float bodyX  = cx + labelW;
                float bodyW  = Math.max(40f, maxTextWidth - labelW);

                drawText(cs, corpLabel, cx, yPtr, PDType1Font.HELVETICA_BOLD, 10);

                {
                    String[] words = corpBody.split("\\s+");
                    StringBuilder line = new StringBuilder();
                    float yLine = yPtr;
                    for (String w : words) {
                        String cand = (line + " " + w).trim();
                        if (stringWidth(cand, PDType1Font.HELVETICA, 10) <= bodyW) {
                            line = new StringBuilder(cand);
                        } else {
                            drawText(cs, line.toString(), bodyX, yLine, PDType1Font.HELVETICA, 10);
                            yLine -= (LINE_H - 2);
                            line = new StringBuilder(w);
                        }
                    }
                    if (line.length() > 0) {
                        drawText(cs, line.toString(), bodyX, yLine, PDType1Font.HELVETICA, 10);
                        yLine -= (LINE_H - 2);
                    }
                    yPtr = yLine;
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }


    private static PDImageXObject loadImage(PDDocument doc, String resourcePath) {
        try (InputStream is = PdfGenerator.class.getResourceAsStream(resourcePath)) {
            if (is == null) return null;
            byte[] bytes = is.readAllBytes();
            return PDImageXObject.createFromByteArray(doc, bytes, resourcePath);
        } catch (IOException e) {
            return null;
        }
    }

    private static void drawText(PDPageContentStream cs, String text, float x, float y,
                                 org.apache.pdfbox.pdmodel.font.PDFont font, int size) throws IOException {
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text != null ? text : "");
        cs.endText();
    }

    private static float drawCenteredText(PDPageContentStream cs, String text,
                                          float xLeft, float xRight, float y,
                                          org.apache.pdfbox.pdmodel.font.PDFont font,
                                          int size) throws IOException {
        float textWidth = stringWidth(text, font, size);
        float startX = xLeft + ((xRight - xLeft) - textWidth) / 2f;
        drawText(cs, text, startX, y, font, size);
        return y - (size + (LINE_H - size));
    }

    private static void drawCenteredBetween(PDPageContentStream cs, String text,
                                            float x1, float x2, float y,
                                            org.apache.pdfbox.pdmodel.font.PDFont font,
                                            int size) throws IOException {
        float w = stringWidth(text, font, size);
        float startX = x1 + ((x2 - x1) - w) / 2f;
        drawText(cs, text, startX, y, font, size);
    }

    private static void drawLine(PDPageContentStream cs, float x1, float y1, float x2, float y2) throws IOException {
        cs.moveTo(x1, y1);
        cs.lineTo(x2, y2);
        cs.stroke();
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
}
