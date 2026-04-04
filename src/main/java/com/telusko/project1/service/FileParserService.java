package com.telusko.project1.service;

import com.lowagie.text.Anchor;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import java.awt.Color;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class FileParserService {

    public String extractTextFromFile(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("File must have a name");
        }

        filename = filename.toLowerCase();
        
        try (InputStream inputStream = file.getInputStream()) {
            if (filename.endsWith(".pdf")) {
                return extractTextFromPdf(inputStream);
            } else if (filename.endsWith(".docx")) {
                return extractTextFromDocx(inputStream);
            } else if (filename.endsWith(".txt")) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            } else {
                throw new IllegalArgumentException("Unsupported file format. Please upload .pdf, .docx, or .txt");
            }
        }
    }

    private String extractTextFromPdf(InputStream inputStream) throws Exception {
        try (PDDocument document = org.apache.pdfbox.Loader.loadPDF(inputStream.readAllBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extractTextFromDocx(InputStream inputStream) throws Exception {
        try (XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }

    public byte[] generatePdfFromText(String text) throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            document.setMargins(40, 40, 40, 40); // Professional resume margins
            PdfWriter.getInstance(document, out);
            document.open();

            Font nameFont = new Font(Font.TIMES_ROMAN, 18, Font.BOLD);
            Font headerFont = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
            Font bodyFont = new Font(Font.TIMES_ROMAN, 11, Font.NORMAL);
            Font linkFont = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL, Color.BLUE);
            Font contactFont = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);

            String[] lines = text.split("\n");
            boolean isFirstLine = true;
            boolean inContactZone = true;

            for (String line : lines) {
                line = line.replace("\r", "").trim();
                if (line.isEmpty()) {
                    continue;
                }

                Paragraph p = new Paragraph();

                // 1. Center the Name
                if (isFirstLine) {
                    p.setAlignment(Element.ALIGN_CENTER);
                    p.add(new Chunk(line.replace("**", ""), nameFont));
                    p.setSpacingAfter(4);
                    document.add(p);
                    isFirstLine = false;
                    continue;
                }

                String upperLine = line.toUpperCase();
                boolean isHeaderFlag = (line.startsWith("**") && line.endsWith("**") && line.length() < 40)
                        || (line.endsWith(":") && line.split(" ").length <= 4)
                        || (upperLine.equals(line) && line.length() > 3 && line.length() < 30);

                if (isHeaderFlag || line.length() > 60 || line.startsWith("-") || line.startsWith("*")) {
                    inContactZone = false; // We reached the real resume content
                }

                // 2. Center Contact Info (Detect up to the first blank section)
                if (inContactZone) {
                    p.setAlignment(Element.ALIGN_CENTER);
                    parseLineWithLinks(p, line, contactFont, linkFont);
                    p.setSpacingAfter(6);
                    document.add(p);
                    continue;
                }

                // 3. Section Headers
                if (isHeaderFlag) {
                    String cleanHeader = line.replace("**", "").replace(":", "").trim().toUpperCase();
                    p.add(new Chunk(cleanHeader, headerFont));
                    p.setSpacingBefore(8);
                    document.add(p);
                    
                    // Add solid line separator
                    LineSeparator ls = new LineSeparator();
                    ls.setLineWidth(0.8f);
                    document.add(new Chunk(ls));
                    
                    document.add(new Paragraph(" ")); // Spacer
                    continue;
                }

                // 4. Normal Body Text (with basic internal bolding & link parsing)
                if (line.startsWith("- ") || line.startsWith("* ")) {
                    p.setIndentationLeft(15f); // Beautiful bullet indent
                } else {
                    p.setSpacingBefore(2);
                }
                
                p.setSpacingAfter(4);
                p.setAlignment(Element.ALIGN_LEFT); // Standard resume alignment
                parseNormalBodyLine(p, line, bodyFont, linkFont);
                document.add(p);
            }

            document.close();
            return out.toByteArray();
        }
    }

    private void parseLineWithLinks(Paragraph p, String line, Font normalFont, Font linkFont) {
        String[] words = line.split(" ");
        for (String word : words) {
            String cleanWord = word.replace("**", "");
            if (cleanWord.startsWith("http://") || cleanWord.startsWith("https://") || cleanWord.startsWith("www.")) {
                Anchor anchor = new Anchor(cleanWord + " ", linkFont);
                anchor.setReference(cleanWord.startsWith("www.") ? "http://" + cleanWord : cleanWord);
                p.add(anchor);
            } else if (cleanWord.contains("@") && cleanWord.contains(".")) {
                Anchor anchor = new Anchor(cleanWord + " ", linkFont);
                anchor.setReference("mailto:" + cleanWord);
                p.add(anchor);
            } else {
                p.add(new Chunk(cleanWord + " ", normalFont));
            }
        }
    }

    private void parseNormalBodyLine(Paragraph p, String line, Font normalFont, Font linkFont) {
        // Simple bold parser mixed with links
        String[] parts = line.split("\\*\\*");
        for (int i = 0; i < parts.length; i++) {
            boolean isBold = (i % 2 == 1);
            Font currentFont = isBold ? new Font(Font.HELVETICA, 11, Font.BOLD) : normalFont;
            
            // Sub-parse this chunk for links
            parseLineWithLinks(p, parts[i], currentFont, linkFont);
        }
    }
}
