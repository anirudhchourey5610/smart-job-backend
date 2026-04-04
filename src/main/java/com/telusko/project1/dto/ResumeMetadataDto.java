package com.telusko.project1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeMetadataDto {
    private String fileName;
    private String uploadDate;
    private String fileSize;
}
