package com.telusko.project1.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProjectDto {
    private String name;
    private String techStack;
    private String link;
    private List<String> items;
}
