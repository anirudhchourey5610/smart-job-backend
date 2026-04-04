package com.telusko.project1.service;

import com.telusko.project1.model.JobPost;
import com.telusko.project1.repository.JobPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class JobFetcherService {

    @Autowired
    private JobPostRepository jobPostRepository;
    @Scheduled(cron = "0 0 0/12 * * ?")
    public void fetchJobsFromRemotive() {
        // Switched from Remotive to Jobicy to guarantee plenty of REAL jobs with salaries!
        String url = "https://jobicy.com/api/v2/remote-jobs?count=50&geo=apac";
        RestTemplate restTemplate = new RestTemplate();
        int totalFound = 0;

        System.out.println("Fetching REAL remote jobs from Jobicy API...");
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("jobs")) {
                List<Map<String, Object>> jobsList = (List<Map<String, Object>>) response.get("jobs");
                for (Map<String, Object> jobData : jobsList) {
                    Long externalId = Long.valueOf(jobData.get("id").toString());

                    // Skip if job already exists
                    if (jobPostRepository.existsByExternalId(externalId)) continue;

                    String companyName = (String) jobData.get("companyName");
                    
                    // Extract Salary (Jobicy uses min/max integers instead of strings)
                    Object minObj = jobData.get("annualSalaryMin");
                    if (minObj == null) minObj = jobData.get("salaryMin");
                    Object maxObj = jobData.get("annualSalaryMax");
                    if (maxObj == null) maxObj = jobData.get("salaryMax");
                    
                    String salary = null;
                    if (minObj != null && maxObj != null) {
                        String currency = (String) jobData.get("salaryCurrency");
                        if (currency == null) currency = "$";
                        salary = currency + minObj + " - " + currency + maxObj;
                    }

                    // MANDATORY FILTERING
                    if (companyName == null || companyName.trim().isEmpty()) continue;
                    if (salary == null || salary.trim().isEmpty()) continue;

                    // LOCATION FILTERING
                    String location = (String) jobData.get("jobGeo");
                    if (location == null || location.trim().isEmpty()) continue; 
                    
                    String locLower = location.toLowerCase();
                    if (!locLower.contains("india") && !locLower.contains("apac") && !locLower.contains("worldwide") && !locLower.contains("anywhere")) {
                        continue; 
                    }

                    JobPost job = new JobPost();
                    job.setExternalId(externalId);
                    job.setTitle((String) jobData.get("jobTitle"));
                    job.setCompanyName(companyName);
                    job.setApplyUrl((String) jobData.get("url"));
                    job.setDescription((String) jobData.get("jobDescription"));
                    job.setSalary(salary);
                    job.setLocation(location);

                    Object teamObj = jobData.get("jobIndustry");
                    if (teamObj instanceof List && !((List<?>) teamObj).isEmpty()) {
                        job.setTeam((String) ((List<?>) teamObj).get(0));
                    }

                    Object typeObj = jobData.get("jobType");
                    if (typeObj instanceof List && !((List<?>) typeObj).isEmpty()) {
                        job.setJobType((String) ((List<?>) typeObj).get(0));
                    }

                    jobPostRepository.save(job);
                    totalFound++;
                }
            }
        } catch (Exception e) {
            System.err.println("API Fetch failed: " + e.getMessage());
        }

        System.out.println("Successfully parsed and saved " + totalFound + " REAL matching jobs from Jobicy!");
    }

    @jakarta.annotation.PostConstruct
    public void executeDataQualityScanOnStartup() {
        System.out.println("Running backend data quality scan on existing database...");
        List<JobPost> allJobs = jobPostRepository.findAll();
        int removedCount = 0;
        for (JobPost job : allJobs) {
            boolean isBadJob = false;
            
            // Check mandatory properties
            if (job.getSalary() == null || job.getSalary().trim().isEmpty() ||
                job.getCompanyName() == null || job.getCompanyName().trim().isEmpty() ||
                job.getLocation() == null) {
                isBadJob = true;
            } else {
                // Check if location applies to India
                String locLower = job.getLocation().toLowerCase();
                if (!locLower.contains("india") && !locLower.contains("worldwide") && !locLower.contains("anywhere")) {
                    isBadJob = true;
                }
            }

            if (isBadJob) {
                jobPostRepository.delete(job);
                removedCount++;
            }
        }
        System.out.println("Scan complete. Purged " + removedCount + " invalid jobs from Database.");
    }
}
