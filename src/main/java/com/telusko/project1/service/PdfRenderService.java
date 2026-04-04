package com.telusko.project1.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telusko.project1.dto.ResumeContentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;

@Service
public class PdfRenderService {

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private ObjectMapper objectMapper;

    public byte[] generatePdfFromJson(String jsonString) throws Exception {
        // 1. Parse JSON to DTO
        ResumeContentDto resumeData = objectMapper.readValue(jsonString, ResumeContentDto.class);

        // 2. Load data into Thymeleaf context
        Context context = new Context();
        context.setVariable("resume", resumeData);

        // 3. Render HTML template (src/main/resources/templates/resume.html)
        String htmlContent = templateEngine.process("resume", context);

        // 4. Convert HTML to PDF using Flying Saucer
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            
            // Flying saucer requires strict XHTML
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        }
    }
}
