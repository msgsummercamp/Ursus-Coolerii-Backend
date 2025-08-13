package com.example.airassist.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface MailSenderService {
    void sendMailWithPass(String to, String password);
    void sendMailWithCaseAndPdf(String to, String caseId, byte[] pdfBytes);
}
