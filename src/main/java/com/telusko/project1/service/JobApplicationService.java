package com.telusko.project1.service;

import com.telusko.project1.model.EmailTemplate;
import com.telusko.project1.model.JobApplication;
import com.telusko.project1.model.User;
import com.telusko.project1.repository.EmailTemplateRepository;
import com.telusko.project1.repository.JobApplicationRepository;
import com.telusko.project1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class JobApplicationService {
@Autowired
    private JobApplicationRepository jobApplicationRepository;
@Autowired
    private EmailService emailService;
    @Autowired
    private EmailTemplateRepository emailTemplateRepository;
    @Autowired
    private UserRepository userRepository;
    public JobApplication applyForJob(JobApplication jobApplication){
        JobApplication savedApp = jobApplicationRepository.save(jobApplication);
        EmailTemplate template = emailTemplateRepository.findByName("Standard");
        if (template == null) {
            template = new EmailTemplate();
            template.setName("Standard");
            template.setSubject("Application for [ROLE] at [COMPANY]");
            template.setBody("Dear HR Team,\n\nI am writing to apply for the [ROLE] position at [COMPANY]. Please find my resume attached.\n\nBest regards,\nApplicant");
            emailTemplateRepository.save(template);
        }
        User realUser = userRepository.findById(jobApplication.getUser().getId()).orElse(null);
        String candidateName = (realUser != null && realUser.getName() != null) ? realUser.getName() : "Applicant";
        String dynamicSubject = template.getSubject()
                .replace("[ROLE]", jobApplication.getJobRole())
                .replace("[COMPANY]", jobApplication.getCompanyName());

        String dynamicBody = template.getBody()
                .replace("[ROLE]", jobApplication.getJobRole())
                .replace("[COMPANY]", jobApplication.getCompanyName())
                 .replace("Applicant", candidateName);
        String resumeFilePath = jobApplication.getResumeFileName();
        try {
            emailService.sendEmailWithFileAttachment(jobApplication.getHrEmail(), dynamicSubject, dynamicBody, resumeFilePath);
        } catch (Exception e) {
            System.out.println("Error sending email: " + e.getMessage());
        }
        return savedApp;
    }
}
