package com.telusko.project1.dto;

import lombok.Data;
import java.util.List;

@Data
public class ExperienceDto {
    private String title;
    private String company;
    private String date;
    private List<String> items;
}
