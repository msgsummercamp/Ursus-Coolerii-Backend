package com.example.airassist.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface MailSenderService {
    void sendMail(String to, UUID caseFileId);
}
