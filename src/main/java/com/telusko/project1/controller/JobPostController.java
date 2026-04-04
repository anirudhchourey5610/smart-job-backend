package com.telusko.project1.controller;

import com.telusko.project1.model.JobPost;
import com.telusko.project1.repository.JobPostRepository;
import com.telusko.project1.service.JobFetcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobPostController {
    @Autowired
    private JobPostRepository jobPostRepository;
    @Autowired
    private JobFetcherService jobFetcherService;

    @GetMapping
    public List<JobPost> getAllJobs() {
        return jobPostRepository.findAll();
    }

    @GetMapping("/fetch")
    public String fetchJobsManually() {
        System.out.println("Manually triggering job fetch from Remotive...");
        jobFetcherService.fetchJobsFromRemotive();
        return "Job fetch from Remotive triggered! Casting the final net for all India-compatible jobs with salary data.";
    }
}
