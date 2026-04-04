package com.telusko.project1.dto;

import lombok.Data;
import java.util.List;

@Data
public class ResumeContentDto {
    private String name;
    private String objective;
    private ContactDto contact;
    private List<EducationDto> education;
    private List<String> skills;
    private List<ProjectDto> projects;
    private List<ExperienceDto> experience;
    private List<String> achievements;
}
