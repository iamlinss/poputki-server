package org.smirnova.poputka.services;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void sendMessage(String toEmail, String subject, String body);
}
