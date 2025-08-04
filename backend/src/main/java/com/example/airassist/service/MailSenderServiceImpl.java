package com.example.airassist.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class MailSenderServiceImpl implements MailSenderService {
    private final JavaMailSenderImpl mailSender;

    @Value("${spring.mail.username}")
    private String username;

    public MailSenderServiceImpl(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendMail(String to, UUID caseFileId) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(this.username);
        message.setTo(to);
        message.setSubject("Case File Sent");
        message.setText(String.format("Case file with unique id %s.", caseFileId));

        this.mailSender.send(message);
    }
}