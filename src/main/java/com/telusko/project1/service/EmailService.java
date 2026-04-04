package com.telusko.project1.service;

import com.telusko.project1.model.EmailLog;
import com.telusko.project1.repository.EmailLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import java.io.File;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    
    @Autowired
    private EmailLogRepository emailLogRepository;

    public void sendSimpleEmail(String toEmail, String subject, String body) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("uchourey16@gmail.com");
        message.setSubject("Application for Software Developer at SoftSolutions – Lion");
        message.setText("Dear Hiring Manager,\n" +
                "\n" +
                "I hope you are doing well. I am writing to express my interest in the Software Developer position at SoftSolutions. I believe my skills and background align well with the requirements of this role.\n" +
                "\n" +
                "Please find my resume attached for your review. I would welcome the opportunity to discuss how I can contribute to your team.\n" +
                "\n" +
                "Thank you for your time and consideration.\n" +
                "\n" +
                "Best regards,\n" +
                "Lion");
          message.setFrom("a27792652@gmail.com");
        javaMailSender.send(message);

        System.out.println("Mail sent successfully to: " + toEmail);
    }

    public void sendEmailWithAttachment(String to, String subject, String body, String attachmentFilePath) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);

            org.springframework.core.io.FileSystemResource file = new org.springframework.core.io.FileSystemResource(new java.io.File(attachmentFilePath));
            helper.addAttachment(file.getFilename(), file);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            System.out.println("Failed to send email with attachment: " + e.getMessage());
        }
    }

    public void sendEmailWithPdfAttachment(String to, String subject, String body, byte[] pdfBytes) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);

        org.springframework.core.io.ByteArrayResource resource = new org.springframework.core.io.ByteArrayResource(pdfBytes);
        helper.addAttachment("Optimized_Resume.pdf", resource);

        javaMailSender.send(message);
    }

}
