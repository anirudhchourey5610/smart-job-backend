package com.telusko.project1.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiService {
    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.api.key}")
    private String apiKey;

    public String optimizeResume(String rawResume, String jobDescription) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model","llama-3.1-8b-instant");

        Map<String, Object> responseFormat = new HashMap<>();
        responseFormat.put("type", "json_object");
        requestBody.put("response_format", responseFormat);

        List<Map<String, String>> messages = new ArrayList<>();

        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are an expert resume writer. Rewrite the provided resume to highlight skills relevant to the Job Description. " +
                "CRITICAL RULES: 1. You MUST output ONLY valid JSON matching exactly this schema: " +
                "{\"name\":\"\",\"objective\":\"\",\"contact\":{\"phone\":\"\",\"email\":\"\",\"linkedin\":\"URL\",\"github\":\"URL\"},\"education\":[{\"degree\":\"\",\"institution\":\"\",\"date\":\"\",\"gpa\":\"\",\"coursework\":\"\"}],\"skills\":[\"\"],\"projects\":[{\"name\":\"\",\"techStack\":\"\",\"link\":\"\",\"items\":[\"\"]}],\"experience\":[{\"title\":\"\",\"company\":\"\",\"date\":\"\",\"items\":[\"\"]}],\"achievements\":[\"\"]}. " +
                "2. The 'items' field in projects and experience MUST be a List of plain Strings, NOT objects. " +
                "3. Preserve all raw URLs. 4. Strictly raw JSON, no markdown blocks.");
        messages.add(systemMessage);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", "Job Description:\n" + jobDescription + "\n\nCandidate Resume:\n" + rawResume);
        messages.add(userMessage);

        requestBody.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        Map<String, Object> response = restTemplate.postForObject(apiUrl, requestEntity, Map.class);

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        Map<String, Object> firstChoice = choices.get(0);
        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");

        return (String) message.get("content");
    }
}
