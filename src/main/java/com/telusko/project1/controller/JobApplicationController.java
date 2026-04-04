package com.telusko.project1.controller;

import com.telusko.project1.dto.FinalApplicationRequest;
import com.telusko.project1.dto.OptimizeRequest;
import com.telusko.project1.model.JobApplication;
import com.telusko.project1.service.AiService;
import com.telusko.project1.service.EmailService;
import com.telusko.project1.service.JobApplicationService;
import com.telusko.project1.service.JobFetcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/job-applications")
public class JobApplicationController {
      @Autowired
      private JobApplicationService jobApplicationService;

     @PostMapping
     public JobApplication applyForJob(@RequestBody JobApplication jobApplication) {
         return jobApplicationService.applyForJob(jobApplication);
     }

    @Autowired
    private JobFetcherService jobFetcherService;
     @Autowired
     private AiService aiService;
    @PostMapping("/optimize")
    public String optimizeResumeDynamic(@RequestBody OptimizeRequest request) {
        return aiService.optimizeResume(request.getRawResume(), request.getJobDescription());
    }
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private com.telusko.project1.service.PdfRenderService pdfRenderService;

    @PostMapping("/apply-optimized")
    public String applyWithOptimizedResume(@RequestBody FinalApplicationRequest request) {
        String subject = "Job Application: " + request.getJobTitle();
        
        String body = String.format(
            "Dear Hiring Manager,\n\n" +
            "I am writing to formally submit my application for the %s position.\n\n" +
            "After reviewing the job description, I am confident that my skills and experiences align perfectly with your requirements. " +
            "I possess a strong technical background and a proven track record of delivering high-quality results.\n\n" +
            "Please find my securely attached resume as a PDF document for your review. " +
            "It details my relevant qualifications, technical proficiencies, and professional milestones.\n\n" +
            "I would welcome the opportunity to discuss how my expertise can add value to your team and contribute to the company's continuous growth. " +
            "I look forward to hearing from you soon.\n\n" +
            "Thank you for your time and consideration.\n\n" +
            "Best regards,\n" +
            "The Applicant\n",
            request.getJobTitle()
        );

        try {
            byte[] pdfBytes = pdfRenderService.generatePdfFromJson(request.getFinalResumeText());
            emailService.sendEmailWithPdfAttachment(request.getHrEmail(), subject, body, pdfBytes);
            return "Success! Your AI-Optimized Resume was attached as a PDF and sent to " + request.getHrEmail() + "!";
        } catch (Exception e) {
            return "Error generating PDF or sending email: " + e.getMessage();
        }
    }

}
