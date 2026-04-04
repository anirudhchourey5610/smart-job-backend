package com.telusko.project1.service;

import com.telusko.project1.model.EmailLog;
import com.telusko.project1.repository.EmailLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import jakarta.mail.MessagingException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Autowired
    private EmailLogRepository emailLogRepository;

    private final String RESEND_API_URL = "https://api.resend.com/emails";

    public void sendSimpleEmail(String toEmail, String subject, String body) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + resendApiKey);

            Map<String, Object> payload = new HashMap<>();
            payload.put("from", "AI Resume <onboarding@resend.dev>");
            payload.put("to", toEmail);
            payload.put("subject", subject);
            payload.put("html", body);

            org.springframework.http.HttpEntity<Map<String, Object>> entity = new org.springframework.http.HttpEntity<>(payload, headers);
            restTemplate.postForLocation(RESEND_API_URL, entity);
            System.out.println("Simple email sent via Resend API to: " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send simple email via Resend API: " + e.getMessage());
        }
    }

    public void sendEmailWithFileAttachment(String to, String subject, String body, String filePath) {
        try {
            File file = new java.io.File(filePath);
            if (!file.exists()) {
                System.err.println("Attachment file not found: " + filePath);
                sendSimpleEmail(to, subject, body);
                return;
            }
            byte[] fileBytes = java.nio.file.Files.readAllBytes(file.toPath());
            sendEmailWithPdfAttachment(to, subject, body, fileBytes);
        } catch (Exception e) {
            System.err.println("Failed to read attachment file: " + e.getMessage());
            sendSimpleEmail(to, subject, body);
        }
    }

    public void sendEmailWithPdfAttachment(String to, String subject, String body, byte[] pdfBytes) throws MessagingException {
        try {
            RestTemplate restTemplate = new RestTemplate();
            
            // Prepare Headers
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + resendApiKey);

            // Prepare Attachment (Base64)
            String base64Content = java.util.Base64.getEncoder().encodeToString(pdfBytes);

            // Prepare Payload
            java.util.Map<String, Object> payload = new java.util.HashMap<>();
            payload.put("from", "AI Resume <onboarding@resend.dev>");
            payload.put("to", to);
            payload.put("subject", subject);
            payload.put("html", body);
            
            java.util.Map<String, String> attachment = new java.util.HashMap<>();
            attachment.put("content", base64Content);
            attachment.put("filename", "Optimized_Resume.pdf");
            
            payload.put("attachments", java.util.Collections.singletonList(attachment));

            org.springframework.http.HttpEntity<java.util.Map<String, Object>> entity = new org.springframework.http.HttpEntity<>(payload, headers);
            
            restTemplate.postForLocation(RESEND_API_URL, entity);
            System.out.println("Email sent successfully via Resend API to: " + to);
            
        } catch (Exception e) {
            System.err.println("Failed to send email via Resend API: " + e.getMessage());
            throw new MessagingException("API Email Failed: " + e.getMessage());
        }
    }

}
