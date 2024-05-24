package com.example.prometei.services;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendSimpleEmail(final String toAddress, final String subject, final String message);
    void sendHtmlEmail(final String toAddress, final Long purchaseId) throws MessagingException;
}