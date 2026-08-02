package com.facilityflow.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
