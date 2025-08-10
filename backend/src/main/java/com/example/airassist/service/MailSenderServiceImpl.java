package com.example.airassist.service;

import jakarta.mail.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
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

    private SimpleMailMessage createGenericMessageToUser(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(this.username);
        message.setTo(to);
        return message;
    }

    @Override
    @Async
    public void sendMailWithPass(String to, String pass) {
        SimpleMailMessage message = createGenericMessageToUser(to);
        message.setSubject("Ursus Coolerii Air Assist - New Account");
        message.setText("Your new account has been created successfully.\n" +
                "Your password is: " + pass + "\n\n" +
                "Please don't forget to change it when you log in for the first time.\n\n" +
                "Best regards,\n" +
                "Ursus Coolerii Air Assist Team");
        this.mailSender.send(message);
    }

    @Override
    @Async
    public void sendMailWithCase(String to, String caseId) {
        SimpleMailMessage message = createGenericMessageToUser(to);
        message.setSubject("Ursus Coolerii Air Assist - Case Update");
        message.setText("Dear user,\n\n" +
                "This is an update regarding your support case.\n" +
                "Your case ID is: " + caseId + "\n\n" +
                "We will notify you of any further updates.\n\n" +
                "Best regards,\n" +
                "Ursus Coolerii Air Assist Team");
        this.mailSender.send(message);
    }


}