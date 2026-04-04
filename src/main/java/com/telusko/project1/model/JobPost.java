package com.telusko.project1.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.context.annotation.Primary;

@Entity
@Data
public class JobPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long externalId;
    private String title;
    private String companyName;
    private String applyUrl;
    private String location;
    private String salary;
    private String team;
    private String jobType;

    @Column(columnDefinition = "TEXT")
    private String description;
}
